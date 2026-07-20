<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="关联主表ID" prop="masterId">
        <el-input
            v-model="queryParams.masterId"
            placeholder="请输入关联主表ID"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="资产名称" prop="assetName">
        <el-input
            v-model="queryParams.assetName"
            placeholder="请输入资产名称"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="盘点结果" prop="inventoryResult">
        <el-select v-model="queryParams.inventoryResult" placeholder="请选择盘点结果" clearable style="width: 150px">
          <el-option
              v-for="dict in inventory_result"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="盘点人" prop="inventoryPerson">
        <el-input
            v-model="queryParams.inventoryPerson"
            placeholder="请输入盘点人"
            clearable
            @keyup.enter="handleQuery"
            style="width: 150px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">清空</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain @click="handleAdd" v-hasPermi="['asset:inventoryDetail:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :disabled="single" @click="handleUpdate"
                   v-hasPermi="['asset:inventoryDetail:edit']">修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                   v-hasPermi="['asset:inventoryDetail:remove']">删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain @click="handleExport" v-hasPermi="['asset:inventoryDetail:export']">导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="inventoryDetailList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column align="center" label="明细ID" prop="id" width="80"/>
      <el-table-column align="center" label="盘点单号" prop="inventoryCode" show-overflow-tooltip
      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.inventoryCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column align="center" label="资产编码" prop="assetCode" show-overflow-tooltip
      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.assetCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column align="center" label="资产名称" prop="assetName" show-overflow-tooltip
      />
      <el-table-column label="账面数量" align="right" prop="theoreticalQuantity" width="100"
      />
      <el-table-column label="实盘数量" align="right" prop="actualQuantity" width="100"
      />
      <el-table-column label="差异数量" align="right" prop="difference" width="100"
      >
        <template #default="scope">
          <span :style="{ color: getDifferenceColor(scope.row.difference), fontWeight: 'bold' }">
            {{ getDifferenceText(scope.row.difference) }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="盘点结果" align="center" prop="inventoryResult" width="100"
      >
        <template #default="scope">
          <dict-tag :options="inventory_result" :value="scope.row.inventoryResult"/>
        </template>
      </el-table-column>
      <el-table-column align="center" label="结果说明" prop="inventoryResultDesc" show-overflow-tooltip
      />
      <el-table-column align="center" label="盘点人" prop="inventoryPersonName" width="100"
      />
      <el-table-column label="盘点时间" align="center" prop="inventoryTime" width="160"
      >
        <template #default="scope">
          <span>{{ parseTime(scope.row.inventoryTime) || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['asset:inventoryDetail:query']">
            详情
          </el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:inventoryDetail:edit']">
            修改
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:inventoryDetail:remove']">
            删除
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

    <!-- 添加或修改资产盘点明细对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="inventoryDetailRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="盘点单号" prop="masterId">
              <el-select v-model="form.masterId" placeholder="请选择盘点单" filterable clearable style="width: 100%">
                <el-option
                    v-for="inventory in inventoryOptions"
                    :key="inventory.id"
                    :label="inventory.inventoryCode"
                    :value="inventory.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="选择资产" prop="assetId">
              <el-select v-model="form.assetId" placeholder="请选择资产" filterable clearable style="width: 100%">
                <el-option
                    v-for="asset in assetOptions"
                    :key="asset.id"
                    :label="`${asset.assetCode} - ${asset.assetName}`"
                    :value="asset.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="账面数量" prop="theoreticalQuantity">
              <el-input-number v-model="form.theoreticalQuantity" :min="0" style="width: 100%" disabled/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实盘数量" prop="actualQuantity">
              <el-input-number v-model="form.actualQuantity" :min="0" style="width: 100%" @change="handleDiffChange"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="差异数量" prop="difference">
              <el-input v-model="form.difference" disabled/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="盘点结果" prop="inventoryResult">
              <el-select v-model="form.inventoryResult" placeholder="请选择盘点结果" style="width: 100%">
                <el-option v-for="dict in inventory_result" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="盘点人" prop="inventoryPerson">
              <el-select v-model="form.inventoryPerson" placeholder="请选择盘点人" filterable clearable
                         style="width: 100%">
                <el-option v-for="user in userOptions" :key="user.userId" :label="user.nickName" :value="user.userId"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="盘点时间" prop="inventoryTime">
              <el-date-picker clearable v-model="form.inventoryTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss"
                              placeholder="请选择盘点时间" style="width: 100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="结果说明" prop="inventoryResultDesc">
              <el-input v-model="form.inventoryResultDesc" type="textarea" :rows="3" placeholder="请输入结果说明"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 资产盘点明细详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="资产盘点明细详情" size="50%" direction="rtl">
      <div class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="明细ID">{{ viewData.id }}</el-descriptions-item>
          <el-descriptions-item label="盘点单号">{{ viewData.inventoryCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="资产编码">{{ viewData.assetCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="资产名称">{{ viewData.assetName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="账面数量">{{ viewData.theoreticalQuantity || '-' }}</el-descriptions-item>
          <el-descriptions-item label="实盘数量">{{ viewData.actualQuantity || '-' }}</el-descriptions-item>
          <el-descriptions-item label="差异数量">{{ getDifferenceText(viewData.difference) }}</el-descriptions-item>
          <el-descriptions-item label="盘点结果">
            <dict-tag :options="inventory_result" :value="viewData.inventoryResult"/>
          </el-descriptions-item>
          <el-descriptions-item label="结果说明" :span="2">{{
              viewData.inventoryResultDesc || '-'
            }}
          </el-descriptions-item>
          <el-descriptions-item label="盘点人">{{ viewData.inventoryPersonName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="盘点时间">{{ parseTime(viewData.inventoryTime) || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script setup name="InventoryDetail">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import {
  addInventoryDetail,
  delInventoryDetail,
  getInventoryDetail,
  listInventoryDetail,
  updateInventoryDetail
} from "@/api/asset/inventoryDetail"
import {listInventory} from "@/api/asset/inventory"
import {listAssets} from "@/api/asset/assets"
import {parseTime} from '@/utils/ruoyi'
import {useUserOptions} from '@/composables/asset/useUserOptions'

const {proxy} = getCurrentInstance()
const {inventory_result} = proxy.useDict('inventory_result')

const inventoryDetailList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const drawerVisible = ref(false)
const viewData = ref({})

// 使用 Composable 加载用户选项
const {userOptions, loadUserOptions} = useUserOptions()
// 下拉选项数据
const inventoryOptions = ref([])
const assetOptions = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    masterId: undefined,
    assetName: undefined,
    inventoryResult: undefined,
    inventoryPerson: undefined,
  },
  rules: {
    masterId: [{required: true, message: "请选择盘点单", trigger: "change"}],
    assetId: [{required: true, message: "请选择资产", trigger: "change"}],
    actualQuantity: [{required: true, message: "实盘数量不能为空", trigger: "blur"}],
  }
})

const {queryParams, form, rules} = toRefs(data)

function getDifferenceColor(diff) {
  const numDiff = Number(diff)
  if (numDiff > 0) return '#67c23a'
  if (numDiff < 0) return '#f56c6c'
  return '#909399'
}

function getDifferenceText(diff) {
  const numDiff = Number(diff)
  if (numDiff > 0) return `+${numDiff}`
  if (numDiff < 0) return `${numDiff}`
  return '0'
}

function handleDiffChange() {
  const theoretical = Number(form.value.theoreticalQuantity) || 0
  const actual = Number(form.value.actualQuantity) || 0
  const diff = actual - theoretical
  form.value.difference = diff
  if (diff === 0) {
    form.value.inventoryResult = 'NORMAL'
  } else if (diff > 0) {
    form.value.inventoryResult = 'SURPLUS'
  } else {
    form.value.inventoryResult = 'MISSING'
  }
}

// 加载盘点单列表
function loadInventoryOptions() {
  listInventory({pageNum: 1, pageSize: 100}).then(response => {
    inventoryOptions.value = response.rows || []
  })
}

// 加载资产列表
function loadAssetOptions() {
  listAssets({pageNum: 1, pageSize: 1000}).then(response => {
    assetOptions.value = response.rows || []
  })
}

function getList() {
  loading.value = true
  listInventoryDetail(queryParams.value).then(response => {
    inventoryDetailList.value = response.rows
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

function handleSelectionChange(selection) {
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
    masterId: null,
    assetId: null,
    theoreticalQuantity: null,
    actualQuantity: null,
    difference: null,
    inventoryResult: null,
    inventoryResultDesc: null,
    inventoryPerson: null,
    inventoryTime: null
  }
  proxy.resetForm("inventoryDetailRef")
}

function handleView(row) {
  viewData.value = row
  drawerVisible.value = true
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加资产盘点明细"
}

function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value[0]
  getInventoryDetail(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改资产盘点明细"
  })
}

function submitForm() {
  proxy.$refs["inventoryDetailRef"].validate((valid) => {
    if (valid) {
      const submitData = form.value.id != null ? updateInventoryDetail(form.value) : addInventoryDetail(form.value)
      submitData.then(() => {
        proxy.$modal.msgSuccess(form.value.id != null ? "修改成功" : "新增成功")
        open.value = false
        getList()
      })
    }
  })
}

function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除资产盘点明细编号为"' + _ids + '"的数据项？').then(function () {
    return delInventoryDetail(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleExport() {
  proxy.download('asset/inventoryDetail/export', {...queryParams.value}, `inventoryDetail_${new Date().getTime()}.xlsx`)
}

onMounted(async () => {
  // 加载用户选项
  await loadUserOptions()

  // 加载列表和其他选项
  getList()
  loadInventoryOptions()
  loadAssetOptions()
})
</script>
