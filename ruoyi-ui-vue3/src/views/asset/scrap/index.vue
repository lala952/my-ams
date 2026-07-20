<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="报废单号" prop="scrapCode">
        <el-input
            v-model="queryParams.scrapCode"
            placeholder="请输入报废单号"
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
      <el-form-item label="报废日期" prop="scrapDate">
        <el-date-picker
            v-model="queryParams.scrapDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择报废日期"
            style="width: 180px"
            clearable
        />
      </el-form-item>
      <el-form-item label="报废状态" prop="scrapStatus">
        <el-select v-model="queryParams.scrapStatus" placeholder="请选择报废状态" clearable style="width: 150px">
          <el-option v-for="dict in scrap_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
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
        <el-button type="primary" plain @click="handleAdd" v-hasPermi="['asset:scrap:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :disabled="single" @click="handleUpdate" v-hasPermi="['asset:scrap:edit']">
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete" v-hasPermi="['asset:scrap:remove']">
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain @click="handleExport" v-hasPermi="['asset:scrap:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="scrapList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column label="报废单号" prop="scrapCode" show-overflow-tooltip sortable
      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.scrapCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column label="资产编码" prop="assetCode" show-overflow-tooltip sortable
      />
      <el-table-column label="资产名称" prop="assetName" show-overflow-tooltip sortable
      />
      <el-table-column align="center" label="报废日期" prop="scrapDate" sortable>
        <template #default="scope">
          <span>{{ parseTime(scope.row.scrapDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="报废原因" prop="scrapReason" show-overflow-tooltip sortable
      />
      <el-table-column align="right" label="报废收入" prop="scrapIncome" sortable>
        <template #default="scope">
          <span>{{ formatMoney(scope.row.scrapIncome) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="right" label="报废费用" prop="scrapCost" sortable>
        <template #default="scope">
          <span>{{ formatMoney(scope.row.scrapCost) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="报废状态" prop="scrapStatus" sortable width="100">
        <template #default="scope">
          <dict-tag :options="scrap_status" :value="scope.row.scrapStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['asset:scrap:query']">详情
          </el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:scrap:edit']">修改
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:scrap:remove']">删除
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

    <!-- 添加或修改资产报废对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="scrapRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报废单号" prop="scrapCode">
              <el-input v-model="form.scrapCode" placeholder="请输入报废单号"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选择资产" prop="assetId">
              <el-select v-model="form.assetId" placeholder="请选择资产" filterable clearable style="width: 100%">
                <el-option v-for="asset in assetOptions" :key="asset.id"
                           :label="`${asset.assetCode} - ${asset.assetName}`" :value="asset.id"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报废日期" prop="scrapDate">
              <el-date-picker clearable v-model="form.scrapDate" type="date" value-format="YYYY-MM-DD"
                              placeholder="请选择报废日期" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报废原因" prop="scrapReason">
              <el-input v-model="form.scrapReason" placeholder="请输入报废原因"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报废收入" prop="scrapIncome">
              <el-input-number v-model="form.scrapIncome" :precision="2" :min="0" style="width: 100%"
                               placeholder="请输入报废收入"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报废费用" prop="scrapCost">
              <el-input-number v-model="form.scrapCost" :precision="2" :min="0" style="width: 100%"
                               placeholder="请输入报废费用"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报废状态" prop="scrapStatus">
              <el-select v-model="form.scrapStatus" placeholder="请选择报废状态" style="width: 100%">
                <el-option v-for="dict in scrap_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入内容"/>
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

    <!-- 资产报废详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="资产报废详情" size="50%" direction="rtl">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="报废单号">{{ viewData.scrapCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资产编码">{{ viewData.assetCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ viewData.assetName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="报废日期">{{
            parseTime(viewData.scrapDate, '{y}-{m}-{d}') || '-'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="报废原因">{{ viewData.scrapReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="报废收入">{{ formatMoney(viewData.scrapIncome) }}</el-descriptions-item>
        <el-descriptions-item label="报废费用">{{ formatMoney(viewData.scrapCost) }}</el-descriptions-item>
        <el-descriptions-item label="报废状态">
          <dict-tag :options="scrap_status" :value="viewData.scrapStatus"/>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup lang="ts" name="Scrap">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import type {Scrap, ScrapQueryParams} from "@/types/api/asset/scrap"
import {addScrap, delScrap, getScrap, listScrap, updateScrap} from "@/api/asset/scrap"
import {listAssets} from "@/api/asset/assets"
import {parseTime} from '@/utils/ruoyi'
import {formatMoney} from '@/utils/asset'

const {proxy} = getCurrentInstance()
const {scrap_status} = proxy.useDict('scrap_status')

const scrapList = ref<Scrap[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const ids = ref<number[]>([])
const single = ref<boolean>(true)
const multiple = ref<boolean>(true)
const total = ref<number>(0)
const title = ref<string>("")
const drawerVisible = ref<boolean>(false)
const viewData = ref<Scrap>({} as Scrap)

// 资产选项
const assetOptions = ref([])

const data = reactive({
  form: {} as Scrap,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    scrapCode: undefined,
    assetName: undefined,
    scrapDate: undefined,
    scrapStatus: undefined,
  } as ScrapQueryParams,
  rules: {
    scrapCode: [{required: true, message: "报废单号不能为空", trigger: "blur"}],
    assetId: [{required: true, message: "请选择资产", trigger: "change"}],
    scrapDate: [{required: true, message: "报废日期不能为空", trigger: "change"}],
  }
})

const {queryParams, form, rules} = toRefs(data)

// 加载资产列表
function loadAssetOptions() {
  listAssets({pageNum: 1, pageSize: 1000}).then(response => {
    assetOptions.value = response.rows || []
  })
}

function getList() {
  loading.value = true
  listScrap(queryParams.value).then(response => {
    scrapList.value = response.rows
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

function handleSelectionChange(selection: Scrap[]) {
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
    scrapCode: null,
    assetId: null,
    scrapDate: null,
    scrapReason: null,
    scrapIncome: null,
    scrapCost: null,
    scrapStatus: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  }
  proxy.resetForm("scrapRef")
}

function handleView(row: Scrap) {
  viewData.value = row
  drawerVisible.value = true
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加资产报废"
}

function handleUpdate(row: Scrap) {
  reset()
  const _id = row.id || ids.value[0]
  getScrap(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改资产报废"
  })
}

function submitForm() {
  proxy.$refs["scrapRef"].validate((valid: boolean) => {
    if (valid) {
      const submitData = form.value.id != null ? updateScrap(form.value) : addScrap(form.value)
      submitData.then(() => {
        proxy.$modal.msgSuccess(form.value.id != null ? "修改成功" : "新增成功")
        open.value = false
        getList()
      })
    }
  })
}

function handleDelete(row: Scrap) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除资产报废编号为"' + _ids + '"的数据项？').then(function () {
    return delScrap(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleExport() {
  proxy.download('asset/scrap/export', {...queryParams.value}, `scrap_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList()
  loadAssetOptions()
})
</script>
