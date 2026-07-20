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
      <el-form-item label="变动单编码" prop="changeCode">
        <el-input v-model="queryParams.changeCode" placeholder="请输入变动单编码" clearable @keyup.enter="handleQuery"
                  style="width: 180px"/>
      </el-form-item>
      <el-form-item label="变动类型" prop="changeType">
        <el-select v-model="queryParams.changeType" clearable placeholder="请选择变动类型" style="width: 180px">
          <el-option v-for="item in change_type" :key="item.value" :label="item.label" :value="item.value"/>
        </el-select>
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
        <el-button v-hasPermi="['asset:change:add']" plain type="primary" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                   v-hasPermi="['asset:change:remove']">批量删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain @click="handleExport"
                   v-hasPermi="['asset:change:export']">导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="changeList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column label="变动单编码" prop="changeCode" show-overflow-tooltip>
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.changeCode }}</a>
        </template>
      </el-table-column>

      <el-table-column label="审批状态" prop="businessStatus">
        <template #default="scope">
          <ApprovalStatusBubble
              :business-status="scope.row.businessStatus"
              :proc-inst-id="scope.row.procInstId"
          />
        </template>
      </el-table-column>
          <el-table-column label="变动类型" prop="changeType">
            <template #default="scope">
              <dict-tag :options="change_type" :value="scope.row.changeType"/>
            </template>
          </el-table-column>
      <el-table-column label="申请人" prop="applicantName">
        <template #default="scope">{{scope.row.applicantName}}</template>
      </el-table-column>
      <el-table-column label="申请部门" prop="applicantId">
        <template #default="scope">{{ scope.row.applicantDeptName}}</template>
      </el-table-column>
      <el-table-column label="申请日期" prop="applyDate">
        <template #default="scope">
          <span>{{ parseTime(scope.row.applyDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="变动原因" prop="changeReason" show-overflow-tooltip/>
      <el-table-column label="操作" min-width="90" align="center" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)" >详情</el-button>
          <el-button link type="primary" @click="handleExportPdf(scope.row)">打印
          </el-button>
          <el-button link type="primary" @click="handleEdit(scope.row)"
                     v-if="scope.row.businessStatus === 'draft' || scope.row.businessStatus === 'rejected'"
                     v-hasPermi="['asset:change:edit']">编辑
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-if="scope.row.businessStatus === 'draft'"
                     v-hasPermi="['asset:change:remove']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize"
                @pagination="getList"/>
  </div>
</template>

<script setup name="ChangeList">
import {computed, getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {countByStatus, delChange, exportChangePdf, listChange} from '@/api/asset/change'
import ApprovalStatusBubble from '@/components/Asset/BusinessStatusPopover/index.vue'

const {proxy} = getCurrentInstance()
const router = useRouter()

const {change_type, business_status} = proxy.useDict('change_type', 'business_status')

// 根据字典动态生成Tab选项
const statusOptions = computed(() => {
  return business_status.value.map(item => ({
    label: item.label,
    value: item.value
  }))
})


const changeList = ref([])
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
    changeCode: null,
    changeType: null,
    businessStatus: '',
  }
})

const {queryParams} = toRefs(data)

function getList() {
  loading.value = true
  queryParams.value.businessStatus = activeTab.value

  listChange(proxy.addDateRange(queryParams.value, dateRange.value)).then(res => {
    loading.value = false
    changeList.value = res.rows || []
    total.value = res.total || 0
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
        completed: res.data['completed'] || 0,
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

function handleAdd() {
  router.push('/asset/management/change/apply')
}

function handleView(row) {
  router.push({
    path: '/asset/query/center/change/query/detail',
    query: {id: row.id}
  })
}

function handleEdit(row) {
  router.push({path: '/asset/management/change/apply', query: {id: row.id}})
}

function handleDelete(row) {
  const deleteIds = row.id || ids.value
  proxy.$modal.confirm("确认删除所选中的变动单吗？").then(() => {
    return delChange(deleteIds)
  }).then(() => {
    getList()
    getStatusCounts()
    proxy.$modal.msgSuccess('删除成功')
  }).catch(() => {
  })
}

function handleExport() {
  proxy.download('asset/change/export', {
    ...queryParams.value
  }, `资产变动申请_${new Date().getTime()}.xlsx`)
}

function handleExportPdf(row) {
  exportChangePdf(row.id).then((blob) => {
    const url = window.URL.createObjectURL(new Blob([blob], {type: 'application/pdf'}))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download',`资产变动_${row.changeCode}.pdf`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    proxy.$modal.msgSuccess('PDF导出成功')
  }).catch(() => {
    proxy.$modal.msgError('PDF导出失败')
  })
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