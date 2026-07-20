<template>
  <div class="app-container">
    <!-- 搜索区域 -->
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="任务名称" prop="taskName">
          <el-input
            v-model="queryParams.taskName"
            placeholder="请输入任务名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="流程名称" prop="processName">
          <el-input
            v-model="queryParams.processName"
            placeholder="请输入流程名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary"  @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">清空</el-button>
        </el-form-item>
      </el-form>

    <!-- 待办任务列表 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>我的待办任务</span>
          <el-tag type="warning">{{ todoList.length }} 条待办</el-tag>
        </div>
      </template>

      <el-table 
        v-loading="loading" 
        :data="filteredTodoList" 
        border
        stripe
      >
        <el-table-column label="序号" type="index" width="60" align="center" />
        
        <el-table-column label="任务名称" prop="taskName" show-overflow-tooltip sortable>
          <template #default="scope">
            <el-link type="primary" @click="handleView(scope.row)">
              {{ scope.row.taskName }}
            </el-link>
          </template>
        </el-table-column>

        <el-table-column label="流程名称" prop="processName" show-overflow-tooltip sortable/>

        <el-table-column align="center" label="业务单号" prop="businessKey" show-overflow-tooltip sortable>
          <template #default="scope">
            {{ scope.row.businessKey || '-' }}
          </template>
        </el-table-column>

        <el-table-column label="创建时间" prop="createTime" sortable width="160" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column align="center" label="持续时间">
          <template #default="scope">
            {{ formatDuration(scope.row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column label="优先级" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.priority === 50" type="info" size="small">普通</el-tag>
            <el-tag v-else-if="scope.row.priority > 50" type="warning" size="small">高</el-tag>
            <el-tag v-else type="success" size="small">低</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" align="center" fixed="right">
          <template #default="scope">
            <el-button 
              link 
              type="primary"
              @click="handleView(scope.row)"
            >
              查看
            </el-button>
            <el-button 
              link 
              type="primary"
              @click="handleApprove(scope.row)"
            >
              审批
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty 
        v-if="!loading && filteredTodoList.length === 0" 
        description="暂无待办任务" 
        :image-size="120"
      />
    </el-card>
  </div>
</template>

<script setup name="TodoTaskList">
import {computed, reactive, ref} from 'vue'
import {getTodoTaskList} from '@/api/asset/workflow/workflow.js'
import {ElMessage} from 'element-plus'

const loading = ref(false)
const todoList = ref([])

// 查询参数
const queryParams = reactive({
  taskName: '',
  processName: ''
})

// 过滤后的待办列表
const filteredTodoList = computed(() => {
  let list = todoList.value
  
  // 按任务名称过滤
  if (queryParams.taskName) {
    list = list.filter(item => 
      item.taskName && item.taskName.includes(queryParams.taskName)
    )
  }
  
  // 按流程名称过滤
  if (queryParams.processName) {
    list = list.filter(item => 
      item.processName && item.processName.includes(queryParams.processName)
    )
  }
  
  return list
})

/** 查询待办任务列表 */
function getList() {
  loading.value = true
  getTodoTaskList().then(response => {
    todoList.value = response.data || []
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  // 计算属性会自动更新，这里不需要额外操作
}

/** 重置按钮操作 */
function resetQuery() {
  queryParams.taskName = ''
  queryParams.processName = ''
}

/** 查看详情 */
function handleView(row) {
  // 根据业务类型跳转到不同的详情页
  if (row.processDefinitionKey === 'asset_change_process') {
    // 资产变更流程
    window.open(`/asset/change/detail/${row.businessKey}`, '_blank')
  } else {
    ElMessage.info('该流程类型的详情页面暂未实现')
  }
}

/** 审批任务 */
function handleApprove(row) {
  // 根据业务类型跳转到不同的审批页面
  if (row.processDefinitionKey === 'asset_change_process') {
    // 资产变更流程
    window.open(`/asset/change/approve/${row.businessKey}?taskId=${row.taskId}`, '_blank')
  } else {
    ElMessage.info('该流程类型的审批页面暂未实现')
  }
}

/** 格式化持续时间 */
function formatDuration(createTime) {
  if (!createTime) return '-'
  
  const now = new Date()
  const create = new Date(createTime)
  const diff = now - create
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
  const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
  
  if (days > 0) {
    return `${days}天${hours}小时`
  } else if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  } else {
    return `${minutes}分钟`
  }
}

// 初始化加载数据
getList()
</script>

<style scoped lang="scss">
.search-card {
  margin-bottom: 16px;
}

.table-card {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-weight: 500;
  }
}

.el-table {
  :deep(.el-link) {
    font-weight: 500;
  }
}
</style>
