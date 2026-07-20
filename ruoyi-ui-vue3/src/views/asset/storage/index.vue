<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="入库单号" prop="storageCode">
        <el-input v-model="queryParams.storageCode" placeholder="请输入入库单号" clearable @keyup.enter="handleQuery"
                  style="width: 180px"/>
      </el-form-item>
      <el-form-item label="申请人" prop="applicantName">
        <el-input v-model="queryParams.applicantName" placeholder="请输入申请人" clearable @keyup.enter="handleQuery"
                  style="width: 180px"/>
      </el-form-item>
      <el-form-item label="状态" prop="businessStatus">
        <el-select v-model="queryParams.businessStatus" placeholder="请选择" clearable style="width: 180px">
          <el-option label="草稿" value="draft"/>
          <el-option label="待审批" value="pending"/>
          <el-option label="已退回" value="rejected"/>
          <el-option label="已完成" value="completed"/>
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
        <el-button type="primary" plain @click="handleAdd" v-hasPermi="['asset:storage:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :disabled="single" @click="handleUpdate" v-hasPermi="['asset:storage:edit']">
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete" v-hasPermi="['asset:storage:remove']">
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain @click="handleExport" v-hasPermi="['asset:storage:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="storageList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column label="入库单号" prop="storageCode" show-overflow-tooltip sortable>
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.storageCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column align="center" label="入库类型" prop="storageType" sortable>
        <template #default="scope">
          <dict-tag :options="storage_type" :value="scope.row.storageType"/>
        </template>
      </el-table-column>
      <el-table-column label="申请人" prop="applicantName" show-overflow-tooltip sortable/>
      <el-table-column label="申请部门" prop="applicantDeptName" show-overflow-tooltip sortable
      />
      <el-table-column align="center" label="申请日期" prop="applyDate" sortable>
        <template #default="scope">
          <span>{{ parseTime(scope.row.applyDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="right" label="总金额" prop="totalAmount" sortable width="140">
        <template #default="scope">
          <span>{{ formatMoney(scope.row.totalAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="资产数量" prop="assetCount" sortable width="100"/>
      <el-table-column align="center" label="状态" prop="businessStatus" sortable width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.businessStatus === 'draft'" type="info">草稿</el-tag>
          <el-tag v-else-if="scope.row.businessStatus === 'pending'" type="warning">待审批</el-tag>
          <el-tag v-else-if="scope.row.businessStatus === 'rejected'" type="danger">已退回</el-tag>
          <el-tag v-else-if="scope.row.businessStatus === 'completed'" type="success">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="handleView(scope.row)"
                     v-hasPermi="['asset:storage:query']">详情
          </el-button>
          <el-button link type="primary" size="small" @click="handleUpdate(scope.row)"
                     v-if="scope.row.businessStatus === 'draft'" v-hasPermi="['asset:storage:edit']">编辑
          </el-button>
          <el-button link type="danger" size="small" @click="handleDelete(scope.row)"
                     v-if="scope.row.businessStatus === 'draft'" v-hasPermi="['asset:storage:remove']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize" @pagination="getList"/>

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="storageRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="入库单号" prop="storageCode">
          <el-input v-model="form.storageCode" placeholder="系统自动生成" disabled/>
        </el-form-item>
        <el-form-item label="入库类型" prop="storageType">
          <el-select v-model="form.storageType" placeholder="请选择入库类型" style="width: 100%">
            <el-option label="采购入库" value="purchase"/>
            <el-option label="调拨入库" value="transfer"/>
            <el-option label="归还入库" value="return"/>
          </el-select>
        </el-form-item>
        <el-form-item label="申请人" prop="applicantName">
          <el-input v-model="form.applicantName" placeholder="请输入申请人"/>
        </el-form-item>
        <el-form-item label="申请部门" prop="applicantDeptName">
          <el-input v-model="form.applicantDeptName" placeholder="请输入申请部门"/>
        </el-form-item>
        <el-form-item label="申请日期" prop="applyDate">
          <el-date-picker v-model="form.applyDate" type="date" placeholder="请选择申请日期" style="width: 100%"
                          value-format="YYYY-MM-DD"/>
        </el-form-item>
        <el-form-item label="总金额" prop="totalAmount">
          <el-input-number v-model="form.totalAmount" :min="0" :precision="2" style="width: 100%"
                           placeholder="请输入总金额"/>
        </el-form-item>
        <el-form-item label="资产数量" prop="assetCount">
          <el-input-number v-model="form.assetCount" :min="0" style="width: 100%" placeholder="请输入资产数量"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="1" placeholder="请输入备注" show-word-limit
                    maxlength="500"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="入库详情" size="50%" direction="rtl">
      <div class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="入库单号">{{ viewData.storageCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="入库类型">
            <dict-tag :options="storage_type" :value="viewData.storageType"/>
          </el-descriptions-item>
          <el-descriptions-item label="申请人">{{ viewData.applicantName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请部门">{{ viewData.applicantDeptName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请日期">{{
              parseTime(viewData.applyDate, '{y}-{m}-{d}') || '-'
            }}
          </el-descriptions-item>
          <el-descriptions-item label="总金额">{{ formatMoney(viewData.totalAmount) }}</el-descriptions-item>
          <el-descriptions-item label="资产数量">{{ viewData.assetCount || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="viewData.businessStatus === 'draft'" type="info">草稿</el-tag>
            <el-tag v-else-if="viewData.businessStatus === 'pending'" type="warning">待审批</el-tag>
            <el-tag v-else-if="viewData.businessStatus === 'rejected'" type="danger">已退回</el-tag>
            <el-tag v-else-if="viewData.businessStatus === 'completed'" type="success">已完成</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script setup name="Storage">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {addStorage, delStorage, getStorage, listStorage, updateStorage} from '@/api/asset/storage'

const {proxy} = getCurrentInstance()

const storageList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const open = ref(false)
const drawerVisible = ref(false)
const viewData = ref({})

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    storageCode: null,
    applicantName: null,
    businessStatus: null
  },
  rules: {
    storageType: [{required: true, message: '请选择入库类型', trigger: 'change'}],
    applicantName: [{required: true, message: '请输入申请人', trigger: 'blur'}],
    applyDate: [{required: true, message: '请选择申请日期', trigger: 'change'}]
  }
})

const {form, queryParams, rules} = toRefs(data)

/** 查询列表 */
function getList() {
  loading.value = true
  listStorage(queryParams.value).then(res => {
    storageList.value = res.rows || []
    total.value = res.total || 0
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

/** 格式化金额 */
function formatMoney(value) {
  if (!value && value !== 0) return '-'
  return value.toLocaleString('zh-CN', {minimumFractionDigits: 2, maximumFractionDigits: 2})
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = '新增资产入库'
}

/** 查看详情 */
function handleView(row) {
  getStorage(row.id).then(res => {
    viewData.value = res.data
    drawerVisible.value = true
  })
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  getStorage(id).then(res => {
    form.value = res.data
    open.value = true
    title.value = '修改资产入库'
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const deleteIds = row.id || ids.value
  ElMessageBox.confirm('是否确认删除选中的入库记录？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delStorage(deleteIds)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {
  })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('asset/storage/export', {
    ...queryParams.value
  }, `storage_${new Date().getTime()}.xlsx`)
}

/** 表单重置 */
function reset() {
  form.value = {
    id: null,
    storageCode: null,
    storageType: null,
    applicantName: null,
    applicantDeptName: null,
    applyDate: null,
    totalAmount: 0,
    assetCount: 0,
    remark: null
  }
  proxy.resetForm('storageRef')
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs.storageRef.validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateStorage(form.value).then(res => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addStorage(form.value).then(res => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.detail-content {
  padding: 20px;
}
</style>
