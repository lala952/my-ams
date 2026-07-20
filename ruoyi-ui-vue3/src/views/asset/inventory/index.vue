<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="盘点单号" prop="inventoryCode">
        <el-input
            v-model="queryParams.inventoryCode"
            placeholder="请输入盘点单号"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="盘点名称" prop="inventoryName">
        <el-input
            v-model="queryParams.inventoryName"
            placeholder="请输入盘点名称"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="盘点类型" prop="inventoryType">
        <el-select v-model="queryParams.inventoryType" placeholder="请选择盘点类型" clearable style="width: 150px">
          <el-option
              v-for="dict in inventory_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
          <el-option
              v-for="dict in bill_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">清空</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain @click="handleAdd" v-hasPermi="['asset:inventory:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :disabled="single" @click="handleUpdate" v-hasPermi="['asset:inventory:edit']">
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                   v-hasPermi="['asset:inventory:remove']">删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain @click="handleExport" v-hasPermi="['asset:inventory:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="inventoryList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column align="center" label="盘点单号" min-width="160" prop="inventoryCode" show-overflow-tooltip
                       sortable
      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.inventoryCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column label="盘点名称" prop="inventoryName" show-overflow-tooltip sortable
      />
      <el-table-column align="center" label="盘点类型" prop="inventoryType" sortable width="100">
        <template #default="scope">
          <dict-tag :options="inventory_type" :value="scope.row.inventoryType"/>
        </template>
      </el-table-column>
      <el-table-column align="center" label="开始日期" prop="inventoryStartDate" sortable width="110">
        <template #default="scope">
          <span>{{ parseTime(scope.row.inventoryStartDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="结束日期" prop="inventoryEndDate" sortable width="110">
        <template #default="scope">
          <span>{{ parseTime(scope.row.inventoryEndDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status" sortable width="80">
        <template #default="scope">
          <dict-tag :options="bill_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column align="right" label="应盘总数" prop="totalAssets" sortable width="90"/>
      <el-table-column align="right" label="正常数量" prop="normalCount" sortable width="90"/>
      <el-table-column align="right" label="盘亏数量" prop="missingCount" sortable width="90"/>
      <el-table-column align="right" label="盘盈数量" prop="surplusCount" sortable width="90"/>
      <el-table-column align="right" label="损坏数量" prop="damagedCount" sortable width="90"/>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['asset:inventory:query']">详情
          </el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:inventory:edit']">修改
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:inventory:remove']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 添加或修改盘点管理对话框 -->
    <el-dialog :title="title" v-model="open" width="85%" append-to-body top="5vh">
      <el-form ref="inventoryRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="盘点单号" prop="inventoryCode">
              <el-input disabled v-model="form.inventoryCode" placeholder="自动生成"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="盘点名称" prop="inventoryName">
              <el-input v-model="form.inventoryName" placeholder="请输入盘点名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="盘点类型" prop="inventoryType">
              <el-select v-model="form.inventoryType" placeholder="请选择盘点类型" style="width: 100%">
                <el-option v-for="dict in inventory_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="开始日期" prop="inventoryStartDate">
              <el-date-picker v-model="form.inventoryStartDate" type="date" value-format="YYYY-MM-DD"
                              placeholder="请选择开始日期" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="结束日期" prop="inventoryEndDate">
              <el-date-picker v-model="form.inventoryEndDate" type="date" value-format="YYYY-MM-DD"
                              placeholder="请选择结束日期" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option v-for="dict in bill_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注信息" :rows="2"/>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 资产盘点明细信息 -->
        <el-divider content-position="center">资产盘点明细信息</el-divider>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" @click="handleAddInventoryDetail">添加</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" :disabled="checkedInventoryDetail.length === 0"
                       @click="handleDeleteInventoryDetail">删除选中
            </el-button>
          </el-col>
        </el-row>
        <el-table :data="inventoryDetailList" @selection-change="handleInventoryDetailSelectionChange"
                  ref="inventoryDetail" max-height="400" border>
          <el-table-column type="selection" width="50" align="center"/>
          <el-table-column label="序号" width="55" align="center">
            <template #default="{ $index }">{{ $index + 1 }}</template>
          </el-table-column>
          <el-table-column label="选择资产" prop="assetId" min-width="250">
            <template #default="scope">
              <el-select v-model="scope.row.assetId" placeholder="请选择资产" filterable clearable style="width: 100%"
                         @change="(val) => handleAssetChange(val, scope.row)">
                <el-option v-for="asset in assetsList" :key="asset.id"
                           :label="`${asset.assetCode} - ${asset.assetName}`" :value="asset.id"/>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="资产编码" width="150">
            <template #default="scope">
              <span>{{ getAssetCode(scope.row.assetId) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="账面数量" prop="theoreticalQuantity" width="110">
            <template #default="scope">
              <el-input-number v-model="scope.row.theoreticalQuantity" :min="0" controls-position="right"
                               style="width: 100%" @change="() => handleQuantityChange(scope.row)"/>
            </template>
          </el-table-column>
          <el-table-column label="实盘数量" prop="actualQuantity" width="110">
            <template #default="scope">
              <el-input-number v-model="scope.row.actualQuantity" :min="0" controls-position="right" style="width: 100%"
                               @change="() => handleQuantityChange(scope.row)"/>
            </template>
          </el-table-column>
          <el-table-column label="差异数量" prop="difference" width="100" align="center">
            <template #default="scope">
              <span :style="{ color: getDifferenceColor(scope.row.difference), fontWeight: 'bold' }">
                {{ getDifferenceText(scope.row.difference) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="盘点结果" align="center" prop="inventoryResult" width="100">
            <template #default="scope">
              <dict-tag :options="inventory_result" :value="scope.row.inventoryResult"/>
            </template>
          </el-table-column>
          <el-table-column label="盘点人" prop="inventoryPerson">
            <template #default="scope">
              <el-select v-model="scope.row.inventoryPerson" placeholder="请选择" filterable clearable
                         style="width: 100%">
                <el-option v-for="user in userList" :key="user.userId" :label="user.nickName" :value="user.userId"/>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="盘点时间" prop="inventoryTime" width="160">
            <template #default="scope">
              <el-date-picker v-model="scope.row.inventoryTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
                              placeholder="选择时间" style="width: 100%" :default-value="new Date()"/>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="scope">
              <el-button link type="danger" @click="handleDeleteSingleDetail(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 盘点管理详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="盘点管理详情" size="85%" direction="rtl">
      <div class="drawer-content">
        <!-- 基本信息 -->
        <h4 class="section-title">基本信息</h4>
        <el-descriptions :column="3" border size="large" class="mb-4">
          <el-descriptions-item label="盘点单号">{{ viewData.inventoryCode }}</el-descriptions-item>
          <el-descriptions-item label="盘点名称">{{ viewData.inventoryName }}</el-descriptions-item>
          <el-descriptions-item label="盘点类型">
            <dict-tag :options="inventory_type" :value="viewData.inventoryType"/>
          </el-descriptions-item>
          <el-descriptions-item label="开始日期">{{
              parseTime(viewData.inventoryStartDate, '{y}-{m}-{d}')
            }}
          </el-descriptions-item>
          <el-descriptions-item label="结束日期">{{
              parseTime(viewData.inventoryEndDate, '{y}-{m}-{d}')
            }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <dict-tag :options="bill_status" :value="viewData.status"/>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="3">{{ viewData.remark || '无' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 盘点明细表格 -->
        <h4 class="section-title mt-4">盘点明细（共 {{ viewData.inventoryDetailList?.length || 0 }} 条）</h4>
        <el-table :data="viewData.inventoryDetailList || []" stripe max-height="400" border>
          <el-table-column label="序号" width="60" type="index" align="center"/>
          <el-table-column label="资产信息" min-width="200" show-overflow-tooltip>
            <template #default="scope">
              <div>
                <span>{{ getAssetName(scope.row.assetId) }}</span>
                <el-tag size="small" type="info">{{ getAssetCode(scope.row.assetId) }}</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="账面数量" prop="theoreticalQuantity" width="100" align="center"/>
          <el-table-column label="实盘数量" prop="actualQuantity" width="100" align="center"/>
          <el-table-column label="差异数量" prop="difference" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getDiffTagType(scope.row.difference)" effect="dark" size="small">
                {{ getDifferenceText(scope.row.difference) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="盘点结果" align="center" prop="inventoryResult" width="100">
            <template #default="scope">
              <dict-tag :options="inventory_result" :value="scope.row.inventoryResult"/>
            </template>
          </el-table-column>
          <el-table-column align="center" label="盘点人" prop="inventoryPersonName"/>
          <el-table-column label="盘点时间" prop="inventoryTime" width="160" align="center">
            <template #default="scope">
              {{ parseTime(scope.row.inventoryTime, '{y}-{m}-{d} {h}:{i}') }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts" name="Inventory">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import type {Inventory, InventoryDetail, InventoryQueryParams} from "@/types/api/asset/inventory"
import {addInventory, delInventory, getInventory, listInventory, updateInventory} from "@/api/asset/inventory"
import {listAssets} from "@/api/asset/assets"
import {listUser} from "@/api/system/user"
import {loadAllParams} from "@/api/page"
import {parseTime} from '@/utils/ruoyi'

const {proxy} = getCurrentInstance()
const {
  inventory_type,
  bill_status,
  inventory_result
} = proxy.useDict('inventory_type', 'bill_status', 'inventory_result')

const inventoryList = ref<Inventory[]>([])
const inventoryDetailList = ref<InventoryDetail[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const ids = ref<number[]>([])
const checkedInventoryDetail = ref<any[]>([])
const single = ref<boolean>(true)
const multiple = ref<boolean>(true)
const total = ref<number>(0)
const title = ref<string>("")
const drawerVisible = ref<boolean>(false)
const viewData = ref<Inventory>({} as Inventory)
const assetsList = ref([])
const userList = ref([])

// 资产名称映射
const assetMap = ref(new Map())

const data = reactive({
  form: {} as Inventory,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    inventoryCode: undefined,
    inventoryName: undefined,
    inventoryType: undefined,
    status: undefined,
  } as InventoryQueryParams,
  rules: {
    inventoryName: [{required: true, message: "盘点名称不能为空", trigger: "blur"}],
    inventoryStartDate: [{required: true, message: "开始日期不能为空", trigger: "blur"}],
    inventoryEndDate: [{required: true, message: "结束日期不能为空", trigger: "blur"}],
  }
})

const {queryParams, form, rules} = toRefs(data)

function getDifferenceColor(diff: number | string) {
  const numDiff = Number(diff)
  if (numDiff > 0) return '#67c23a'
  if (numDiff < 0) return '#f56c6c'
  return '#909399'
}

function getDifferenceText(diff: number | string) {
  const numDiff = Number(diff)
  if (numDiff > 0) return `+${numDiff}`
  if (numDiff < 0) return `${numDiff}`
  return '0'
}

function getDiffTagType(diff: number | string) {
  const numDiff = Number(diff)
  if (numDiff > 0) return 'success'
  if (numDiff < 0) return 'danger'
  return 'info'
}

function getAssetCode(assetId: number) {
  const asset = assetsList.value.find(a => a.id === assetId)
  return asset ? asset.assetCode : ''
}

function getAssetName(assetId: number) {
  const asset = assetsList.value.find(a => a.id === assetId)
  return asset ? asset.assetName : ''
}

function handleAssetChange(assetId: number, row: InventoryDetail) {
  const asset = assetsList.value.find(a => a.id === assetId)
  if (asset) {
    row.theoreticalQuantity = asset.quantity || 0
    handleQuantityChange(row)
  }
}

function handleQuantityChange(row: InventoryDetail) {
  const theoretical = Number(row.theoreticalQuantity) || 0
  const actual = Number(row.actualQuantity) || 0
  const diff = actual - theoretical
  row.difference = diff
  if (diff === 0) {
    row.inventoryResult = 'NORMAL'
  } else if (diff > 0) {
    row.inventoryResult = 'SURPLUS'
  } else {
    row.inventoryResult = 'MISSING'
  }
}

function getList() {
  loading.value = true
  listInventory(queryParams.value).then(response => {
    inventoryList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection: Inventory[]) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: null,
    inventoryCode: null,
    inventoryName: null,
    inventoryType: null,
    inventoryStartDate: null,
    inventoryEndDate: null,
    inventoryPersonId: null,
    totalAssets: null,
    normalCount: null,
    missingCount: null,
    surplusCount: null,
    damagedCount: null,
    status: null,
    remark: null
  }
  inventoryDetailList.value = []
  proxy.resetForm("inventoryRef")
}

function handleView(row: Inventory) {
  const id = row.id
  getInventory(id).then(response => {
    viewData.value = response.data
    drawerVisible.value = true
  })
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加盘点管理"
}

function handleUpdate(row: Inventory) {
  reset()
  const _id = row.id || ids.value[0]
  getInventory(_id).then(response => {
    form.value = response.data
    inventoryDetailList.value = response.data?.inventoryDetailList ?? []
    open.value = true
    title.value = "修改盘点管理"
  })
}

function submitForm() {
  proxy.$refs["inventoryRef"].validate((valid: boolean) => {
    if (valid) {
      if (inventoryDetailList.value.length === 0) {
        proxy.$modal.msgError('请至少添加一条盘点明细')
        return
      }
      for (let i = 0; i < inventoryDetailList.value.length; i++) {
        const detail = inventoryDetailList.value[i]
        if (!detail.assetId) {
          proxy.$modal.msgError(`第${i + 1}行资产未选择`)
          return
        }
        if (detail.actualQuantity === undefined || detail.actualQuantity === null) {
          proxy.$modal.msgError(`第${i + 1}行实盘数量未填写`)
          return
        }
      }
      form.value.inventoryDetailList = inventoryDetailList.value
      form.value.totalAssets = inventoryDetailList.value.length
      form.value.normalCount = inventoryDetailList.value.filter(d => d.inventoryResult === 'NORMAL').length
      form.value.missingCount = inventoryDetailList.value.filter(d => d.inventoryResult === 'MISSING').length
      form.value.surplusCount = inventoryDetailList.value.filter(d => d.inventoryResult === 'SURPLUS').length

      const submitData = form.value.id != null ? updateInventory(form.value) : addInventory(form.value)
      submitData.then(() => {
        proxy.$modal.msgSuccess(form.value.id != null ? "修改成功" : "新增成功")
        open.value = false
        getList()
      })
    }
  })
}

function handleDelete(row: Inventory) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除盘点管理编号为"' + _ids + '"的数据项？').then(function () {
    return delInventory(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleAddInventoryDetail() {
  let obj: InventoryDetail = {
    assetId: undefined,
    theoreticalQuantity: 0,
    actualQuantity: 0,
    difference: 0,
    inventoryResult: 'NORMAL',
    inventoryPerson: undefined,
    inventoryTime: new Date().toISOString().replace('T', ' ').substring(0, 19),
  }
  inventoryDetailList.value.push(obj)
}

function handleDeleteSingleDetail(index: number) {
  inventoryDetailList.value.splice(index, 1)
}

function handleDeleteInventoryDetail() {
  if (checkedInventoryDetail.value.length == 0) {
    proxy.$modal.msgError("请先选择要删除的资产盘点明细数据")
  } else {
    inventoryDetailList.value = inventoryDetailList.value.filter(function (item: any, index: number) {
      return !checkedInventoryDetail.value.includes(index)
    })
  }
}

function handleInventoryDetailSelectionChange(selection: any[]) {
  checkedInventoryDetail.value = selection.map(item => item.index)
}

function handleExport() {
  proxy.download('asset/inventory/export', {...queryParams.value}, `资产盘点_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList()
  listAssets(loadAllParams).then(response => {
    assetsList.value = response.rows
  })
  listUser(loadAllParams).then(response => {
    userList.value = response.rows
  })
})
</script>
