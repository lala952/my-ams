<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="调拨单号" prop="transferCode">
        <el-input
            v-model="queryParams.transferCode"
            placeholder="请输入调拨单号"
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
      <el-form-item label="调拨日期" prop="transferDate">
        <el-date-picker
            v-model="queryParams.transferDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择调拨日期"
            style="width: 180px"
            clearable
        />
      </el-form-item>
      <el-form-item label="调拨状态" prop="transferStatus">
        <el-select v-model="queryParams.transferStatus" placeholder="请选择调拨状态" clearable style="width: 150px">
          <el-option v-for="dict in transfer_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
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
        <el-button type="primary" plain @click="handleAdd" v-hasPermi="['asset:transfer:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :disabled="single" @click="handleUpdate" v-hasPermi="['asset:transfer:edit']">
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                   v-hasPermi="['asset:transfer:remove']">删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain @click="handleExport" v-hasPermi="['asset:transfer:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="transferList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column label="调拨单号" prop="transferCode" show-overflow-tooltip sortable
      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.transferCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column label="资产编码" prop="assetCode" show-overflow-tooltip sortable
      />
      <el-table-column label="资产名称" prop="assetName" show-overflow-tooltip sortable
      />
      <el-table-column align="center" label="调拨日期" prop="transferDate" sortable>
        <template #default="scope">
          <span>{{ parseTime(scope.row.transferDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="调出部门" min- prop="outDeptName" show-overflow-tooltip sortable
      />
      <el-table-column label="调入部门" min- prop="inDeptName" show-overflow-tooltip sortable
      />
      <el-table-column label="调出人" prop="outPersonName" sortable width="100"/>
      <el-table-column label="调入人" prop="inPersonName" sortable width="100"/>
      <el-table-column align="center" label="调拨状态" prop="transferStatus" sortable width="100">
        <template #default="scope">
          <dict-tag :options="transfer_status" :value="scope.row.transferStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['asset:transfer:query']">详情
          </el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:transfer:edit']">修改
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:transfer:remove']">删除
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

    <!-- 添加或修改资产调拨对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="transferRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="调拨单号" prop="transferCode">
              <el-input v-model="form.transferCode" placeholder="请输入调拨单号"/>
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
            <el-form-item label="调拨日期" prop="transferDate">
              <el-date-picker clearable v-model="form.transferDate" type="date" value-format="YYYY-MM-DD"
                              placeholder="请选择调拨日期" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="调出部门" prop="outDeptId">
              <el-tree-select v-model="form.outDeptId" :data="deptOptions"
                              :props="{ value: 'deptId', label: 'deptName' }" placeholder="请选择调出部门"
                              check-strictly style="width: 100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="调入部门" prop="inDeptId">
              <el-tree-select v-model="form.inDeptId" :data="deptOptions"
                              :props="{ value: 'deptId', label: 'deptName' }" placeholder="请选择调入部门"
                              check-strictly style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="调出人" prop="outPersonId">
              <el-select v-model="form.outPersonId" placeholder="请选择调出人" filterable clearable style="width: 100%">
                <el-option v-for="user in userOptions" :key="user.userId" :label="user.nickName" :value="user.userId"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="调入人" prop="inPersonId">
              <el-select v-model="form.inPersonId" placeholder="请选择调入人" filterable clearable style="width: 100%">
                <el-option v-for="user in userOptions" :key="user.userId" :label="user.nickName" :value="user.userId"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="调拨状态" prop="transferStatus">
              <el-select v-model="form.transferStatus" placeholder="请选择调拨状态" style="width: 100%">
                <el-option v-for="dict in transfer_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
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

    <!-- 资产调拨详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="资产调拨详情" size="50%" direction="rtl">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="调拨单号">{{ viewData.transferCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资产编码">{{ viewData.assetCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ viewData.assetName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="调拨日期">{{
            parseTime(viewData.transferDate, '{y}-{m}-{d}') || '-'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="调出部门">{{ viewData.outDeptName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="调入部门">{{ viewData.inDeptName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="调出人">{{ viewData.outPersonName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="调入人">{{ viewData.inPersonName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="调拨状态">
          <dict-tag :options="transfer_status" :value="viewData.transferStatus"/>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup name="Transfer">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import {addTransfer, delTransfer, getTransfer, listTransfer, updateTransfer} from "@/api/asset/transfer"
import {listAssets} from "@/api/asset/assets"
import {parseTime} from '@/utils/ruoyi'
import {useUserOptions} from '@/composables/asset/useUserOptions'
import {useDeptOptions} from '@/composables/asset/useDeptOptions'

const {proxy} = getCurrentInstance()
const {transfer_status} = proxy.useDict('transfer_status')

const transferList = ref([])
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

// 使用 Composable 加载选项数据
const {userOptions, loadUserOptions} = useUserOptions()
const {deptOptions, loadDeptOptions} = useDeptOptions()
const assetOptions = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    transferCode: undefined,
    assetName: undefined,
    transferDate: undefined,
    transferStatus: undefined,
  },
  rules: {
    transferCode: [{required: true, message: "调拨单号不能为空", trigger: "blur"}],
    assetId: [{required: true, message: "请选择资产", trigger: "change"}],
    transferDate: [{required: true, message: "调拨日期不能为空", trigger: "change"}],
    outDeptId: [{required: true, message: "请选择调出部门", trigger: "change"}],
    inDeptId: [{required: true, message: "请选择调入部门", trigger: "change"}],
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
  listTransfer(queryParams.value).then(response => {
    transferList.value = response.rows
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
    transferCode: null,
    assetId: null,
    transferDate: null,
    outDeptId: null,
    inDeptId: null,
    outPersonId: null,
    inPersonId: null,
    transferStatus: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  }
  proxy.resetForm("transferRef")
}

function handleView(row) {
  viewData.value = row
  drawerVisible.value = true
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加资产调拨"
}

function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value[0]
  getTransfer(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改资产调拨"
  })
}

function submitForm() {
  proxy.$refs["transferRef"].validate((valid) => {
    if (valid) {
      const submitData = form.value.id != null ? updateTransfer(form.value) : addTransfer(form.value)
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
  proxy.$modal.confirm('是否确认删除资产调拨编号为"' + _ids + '"的数据项？').then(function () {
    return delTransfer(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleExport() {
  proxy.download('asset/transfer/export', {...queryParams.value}, `资产调拨_${new Date().getTime()}.xlsx`)
}

onMounted(async () => {
  // 加载选项数据
  await Promise.all([
    loadUserOptions(),
    loadDeptOptions()
  ])

  // 加载列表和资产选项
  getList()
  loadAssetOptions()
})
</script>
