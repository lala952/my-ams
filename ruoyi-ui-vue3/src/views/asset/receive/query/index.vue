<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane
          v-for="item in statusOptions"
          :key="item.value"
          :name="item.value"
      >
        <template #label>
          <span>{{ item.label }}({{ statusCounts[item.value] || 0 }})</span>
        </template>
      </el-tab-pane>
    </el-tabs>

    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="领用单号" prop="receiveCode">
        <el-input v-model="queryParams.receiveCode" placeholder="请输入领用单号" clearable @keyup.enter="handleQuery"
                  style="width: 180px"/>
      </el-form-item>
      <el-form-item label="申请人" prop="applicantName">
        <el-input v-model="queryParams.applicantName" placeholder="请输入申请人" clearable @keyup.enter="handleQuery"
                  style="width: 180px"/>
      </el-form-item>
      <el-form-item label="申请日期" style="width: 308px">
        <el-date-picker v-model="dateRange" value-format="YYYY-MM-DD" type="daterange" range-separator="-"
                        start-placeholder="开始日期" end-placeholder="结束日期"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">清空</el-button>
      </el-form-item>
    </el-form>

    <!-- 工具栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="info" plain @click="handleExport" v-hasPermi="['asset:receive:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="receiveList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column align="center" label="领用单号" prop="receiveCode" show-overflow-tooltip>
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.receiveCode }}</a>
        </template>
      </el-table-column>
      <el-table-column label="申请人" prop="applicantName" show-overflow-tooltip/>
      <el-table-column label="申请部门" prop="applicantDeptName"
                       show-overflow-tooltip/>
      <el-table-column align="center" label="领用日期" prop="receiveDate">
        <template #default="scope">
          <span>{{ parseTime(scope.row.receiveDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column align="right" label="资产数量" prop="assetCount" width="100"/>
      <el-table-column align="center" label="审批状态" prop="businessStatus">
        <template #default="scope">
          <ApprovalStatusBubble
              :business-status="scope.row.businessStatus"
              :proc-inst-id="scope.row.procInstId"
          />
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column label="操作" align="center" width="150" fixed="right">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="handleView(scope.row)">详情</el-button>
          <el-button link type="primary" size="small" @click="handleExportPdf(scope.row)"
                     v-hasPermi="['asset:receive:export']">打印
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize" @pagination="getList"/>
  </div>
</template>

<script setup name="ReceiveQuery">
import {computed, getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import {countByStatus, listReceive} from '@/api/asset/receive'
import ApprovalStatusBubble from '@/components/Asset/BusinessStatusPopover/index.vue'

const {proxy} = getCurrentInstance()
const router = useRouter()

const {receive_business_status} = proxy.useDict('receive_business_status')

// 根据字典动态生成Tab选项
const statusOptions = computed(() => {
  return receive_business_status.value.map(item => ({
    label: item.label,
    value: item.value
  }))
})

const receiveList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const multiple = ref(true)
const total = ref(0)
const activeTab = ref('draft')
const dateRange = ref([])
const statusCounts = ref({})

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    receiveCode: null,
    applicantName: null,
    businessStatus: ''
  }
})

const {queryParams} = toRefs(data)

function getList() {
  loading.value = true
  queryParams.value.businessStatus = activeTab.value

  listReceive(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    receiveList.value = res.rows || []
    total.value = res.total || 0
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function getStatusCounts() {
  countByStatus().then(res => {
    if (res.code === 200 && res.data) {
      statusCounts.value = {
        draft: res.data['draft'] || 0,
        pending: res.data['pending'] || 0,
        rejected: res.data['rejected'] || 0,
        completed: res.data['completed'] || 0
      }
    }
  })
}

function handleTabChange(tabName) {
  activeTab.value = tabName
  queryParams.value.pageNum = 1
  getList()
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  dateRange.value = []
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  multiple.value = !selection.length
}

function handleView(row) {
  router.push({
    path: '/asset/query/center/receive/query/detail',
    query: {id: row.id}
  })
}

function handleExport() {
  proxy.download('asset/receive/export', {
    ...queryParams.value
  }, `资产领用申请_${new Date().getTime()}.xlsx`)
}

function handleExportPdf(row) {
  ElMessage.info('PDF导出功能待实现')
}

function parseTime(time, pattern) {
  return proxy.parseTime(time, pattern)
}

onMounted(() => {
  getList()
  getStatusCounts()
})
</script>

<style scoped lang="scss">
:deep(.el-tabs__header) {
  margin-bottom: 20px;
}
</style>
