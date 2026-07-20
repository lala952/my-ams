<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="调拨单号" prop="transferCode">
        <el-input v-model="queryParams.transferCode" placeholder="请输入调拨单号" clearable @keyup.enter="handleQuery"
                  style="width: 180px"/>
      </el-form-item>
      <el-form-item label="调出部门" prop="outDeptId">
        <el-tree-select v-model="queryParams.outDeptId" :data="deptOptions" placeholder="请选择调出部门" clearable
                        style="width: 180px"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">清空</el-button>
      </el-form-item>
    </el-form>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="transferList" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column align="center" label="调拨单号" prop="transferCode" show-overflow-tooltip>
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.transferCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column label="资产名称" prop="assetName" show-overflow-tooltip/>
      <el-table-column align="center" label="资产编码" prop="assetCode" show-overflow-tooltip/>
      <el-table-column label="调出部门" prop="outDeptName" show-overflow-tooltip/>
      <el-table-column label="调入部门" prop="inDeptName" show-overflow-tooltip/>
      <el-table-column align="center" label="调拨日期" prop="transferDate">
        <template #default="scope">
          <span>{{ parseTime(scope.row.transferDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="businessStatus" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.businessStatus === 'draft'" type="info">草稿</el-tag>
          <el-tag v-else-if="scope.row.businessStatus === 'pending'" type="warning">待审批</el-tag>
          <el-tag v-else-if="scope.row.businessStatus === 'rejected'" type="danger">已驳回</el-tag>
          <el-tag v-else-if="scope.row.businessStatus === 'completed'" type="success">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="handleView(scope.row)">详情</el-button>
          <el-button link type="success" size="small" @click="handleApprove(scope.row)"
                     v-if="scope.row.businessStatus === 'pending'">审批
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize" @pagination="getList"/>

    <!-- 详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="调拨详情" size="50%" direction="rtl">
      <div class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="调拨单号">{{ viewData.transferCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="资产名称">{{ viewData.assetName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="资产编码">{{ viewData.assetCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="调出部门">{{ viewData.outDeptName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="调入部门">{{ viewData.inDeptName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="调拨日期">{{
              parseTime(viewData.transferDate, '{y}-{m}-{d}') || '-'
            }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="viewData.businessStatus === 'draft'" type="info">草稿</el-tag>
            <el-tag v-else-if="viewData.businessStatus === 'pending'" type="warning">待审批</el-tag>
            <el-tag v-else-if="viewData.businessStatus === 'rejected'" type="danger">已驳回</el-tag>
            <el-tag v-else-if="viewData.businessStatus === 'completed'" type="success">已完成</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>

    <!-- 审批对话框 -->
    <el-dialog title="调拨审批" v-model="approveOpen" width="600px" append-to-body>
      <el-form ref="approveRef" :model="approveForm" :rules="approveRules" label-width="120px">
        <el-form-item label="审批结果" prop="result">
          <el-radio-group v-model="approveForm.result">
            <el-radio label="approved">同意</el-radio>
            <el-radio label="rejected">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="remark">
          <el-input v-model="approveForm.remark" type="textarea" :rows="4" placeholder="请输入审批意见"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="approveOpen = false">取消</el-button>
          <el-button type="primary" @click="submitApprove">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="TransferApproveOut">
import {getCurrentInstance, reactive, ref, toRefs} from 'vue'
import {ElMessage} from 'element-plus'
import {batchApprove, getTransfer, listTransfer} from '@/api/asset/transfer'
import {deptTreeSelect} from '@/api/system/user'

const {proxy} = getCurrentInstance()

const transferList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const total = ref(0)
const drawerVisible = ref(false)
const approveOpen = ref(false)
const viewData = ref({})
const deptOptions = ref([])

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    transferCode: null,
    outDeptId: null,
    businessStatus: 'pending'
  },
  approveForm: {
    ids: [],
    result: 'approved',
    remark: ''
  },
  approveRules: {
    result: [{required: true, message: '请选择审批结果', trigger: 'change'}]
  }
})

const {queryParams, approveForm, approveRules} = toRefs(data)

/** 查询列表 */
function getList() {
  loading.value = true
  listTransfer(queryParams.value).then(res => {
    transferList.value = res.rows || []
    total.value = res.total || 0
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

/** 查询部门树 */
function getDeptTree() {
  deptTreeSelect().then(res => {
    deptOptions.value = res.data
  })
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
}

/** 查看详情 */
function handleView(row) {
  getTransfer(row.id).then(res => {
    viewData.value = res.data
    drawerVisible.value = true
  })
}

/** 审批 */
function handleApprove(row) {
  approveForm.value.ids = [row.id]
  approveForm.value.result = 'approved'
  approveForm.value.remark = ''
  approveOpen.value = true
}

/** 提交审批 */
function submitApprove() {
  proxy.$refs.approveRef.validate(valid => {
    if (valid) {
      batchApprove(approveForm.value).then(res => {
        ElMessage.success('审批成功')
        approveOpen.value = false
        getList()
      })
    }
  })
}

/** 初始化 */
getList()
getDeptTree()
</script>

<style scoped>
.detail-content {
  padding: 20px;
}
</style>
