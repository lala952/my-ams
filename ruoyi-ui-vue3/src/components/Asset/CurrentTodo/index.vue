<template>
  <div class="app-container" v-loading="loading" element-loading-text="加载中……" style="width: auto;height: auto">
    <!-- 无待办任务 -->
    <el-empty v-if="!currentTask" description="暂无待办任务" :image-size="80"/>

    <!-- 有待办任务 -->
    <div v-else class="todo-content">
      <div class="todo-card" :class="{ 'is-urgent': isUrgent }">
        <div class="todo-header">
          <div class="todo-title">
            <el-icon>
              <Flag/>
            </el-icon>
            <span>{{ currentTask.taskName }}</span>
          </div>
          <el-tag type="warning" size="small">待处理</el-tag>
        </div>

        <div class="todo-info">
          <div class="info-row">
            <span class="info-label">流程实例ID：</span>
            <span class="info-value">{{ currentTask.procInstId }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">任务ID：</span>
            <span class="info-value">{{ currentTask.taskId }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">办理人：</span>
            <span class="info-value">{{ currentTask.assigneeName || currentTask.assignee || '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">创建时间：</span>
            <span class="info-value">{{ formatTime(currentTask.createTime) }}</span>
          </div>
        </div>

        <div class="todo-actions">
          <div class="comment-input">
            <el-input
                v-model="approvalForm.comment"
                type="textarea"
                :rows="2"
                placeholder="请输入审批意见..."
                maxlength="200"
                show-word-limit
            />
          </div>
          <div class="action-buttons">
            <el-button type="primary" @click="handleApprove(true)" :loading="submitting">
              同意
            </el-button>
            <el-button type="danger" @click="handleApprove(false)" :loading="submitting">
              驳回
            </el-button>
          </div>
        </div>

        <!-- 常用意见快捷选择 -->
        <div class="quick-comments">
          <span class="quick-label">常用意见：</span>
          <el-tag
              v-for="comment in quickComments"
              :key="comment"
              size="small"
              class="quick-tag"
              @click="approvalForm.comment = comment"
          >
            {{ comment }}
          </el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Flag } from '@element-plus/icons-vue'
import { approveTask, getCurrentTask } from '@/api/asset/workflow/workflow.js'

const props = defineProps({
  // 流程实例ID（可选，如果不传则查询所有待办）
  procInstId: {
    type: String,
    default: ''
  },
  // 是否自动加载
  autoLoad: {
    type: Boolean,
    default: true
  },
  // 是否显示常用意见
  showQuickComments: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['loaded', 'approved', 'error', 'no-task'])

const currentTask = ref(null)
const loading = ref(false)
const submitting = ref(false)
const approvalForm = ref({comment: ''})

// 常用意见
const quickComments = [
  '同意',
  '同意，请按规定办理',
  '情况属实，同意变更',
  '不符合规定，驳回',
  '材料不全，请补充后重新提交',
  '经核实，情况属实，同意'
]

// 判断是否紧急（可根据业务逻辑调整）
const isUrgent = computed(() => {
  if (!currentTask.value?.createTime) return false
  const createTime = new Date(currentTask.value.createTime)
  const now = new Date()
  const hoursDiff = (now - createTime) / (1000 * 60 * 60)
  return hoursDiff > 24 // 超过24小时视为紧急
})

// 时间格式化
const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

/**
 * 加载当前任务
 */
async function loadCurrentTask() {
  if (!props.procInstId) {
    currentTask.value = null
    emit('no-task', '未提供流程实例ID')
    return
  }

  loading.value = true
  try {
    const res = await getCurrentTask(props.procInstId)
    if (res.code === 200 && res.data) {
      currentTask.value = res.data
      emit('loaded', currentTask.value)
    } else {
      currentTask.value = null
      emit('no-task', res.msg || '无待办任务')
    }
  } catch (error) {
    console.error('获取待办任务失败:', error)
    currentTask.value = null
    emit('error', error)
  } finally {
    loading.value = false
  }
}

/**
 * 审批操作
 */
async function handleApprove(approved) {
  if (!currentTask.value) {
    ElMessage.info('无待办任务')
    return
  }

  if (!approvalForm.value.comment) {
    ElMessage.warning('请填写审批意见')
    return
  }

  const actionText = approved ? '通过' : '驳回'
  await ElMessageBox.confirm(
      `确定要${actionText}该申请吗？`,
      '审批确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  )

  submitting.value = true
  try {
    const params = {
      taskId: currentTask.value.taskId,
      approved: approved,
      comment: approvalForm.value.comment
    }
    const res = await approveTask(params)
    if (res.code === 200) {
      ElMessage.success(`审批${actionText}成功`)
      emit('approved', {approved, comment: approvalForm.value.comment, task: currentTask.value})
      // 重新加载任务（流程可能已结束或无新任务）
      await loadCurrentTask()
      // 清空审批意见
      approvalForm.value.comment = ''
    } else {
      ElMessage.error(res.msg || '审批失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审批失败:', error)
      ElMessage.error(error.msg || '审批操作失败')
    }
  } finally {
    submitting.value = false
  }
}

/**
 * 刷新数据
 */
function refresh() {
  loadCurrentTask()
}

// 暴露方法
defineExpose({
  refresh,
  loadCurrentTask
})

// 自动加载
if (props.autoLoad) {
  loadCurrentTask()
}

// 监听 procInstId 变化
watch(() => props.procInstId, () => {
  if (props.autoLoad) {
    loadCurrentTask()
  }
})
</script>

<style scoped lang="scss">
.current-todo {
  width: 100%;
  background: transparent;

  .todo-loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px;
    gap: 12px;
    color: #909399;

    .el-icon {
      font-size: 32px;
    }
  }

  .todo-content {
    width: 100%;
  }

  .todo-card {
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    overflow: hidden;
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    }

    &.is-urgent {
      border-left: 4px solid #f56c6c;
      background: linear-gradient(135deg, #fff 0%, #fff9f9 100%);
    }

    .todo-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 20px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: #fff;

      .todo-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 16px;
        font-weight: 600;

        .el-icon {
          font-size: 18px;
        }
      }
    }

    .todo-info {
      padding: 16px 20px;
      background: #f8f9fa;
      border-bottom: 1px solid #eee;

      .info-row {
        display: flex;
        margin-bottom: 8px;
        font-size: 13px;

        &:last-child {
          margin-bottom: 0;
        }

        .info-label {
          width: 100px;
          color: #909399;
          flex-shrink: 0;
        }

        .info-value {
          color: #303133;
          word-break: break-all;
        }
      }
    }

    .todo-actions {
      padding: 16px 20px;
      border-bottom: 1px solid #eee;

      .comment-input {
        margin-bottom: 12px;
      }

      .action-buttons {
        display: flex;
        gap: 12px;
        justify-content: flex-end;
      }
    }

    .quick-comments {
      padding: 12px 20px;
      background: #fafbfc;
      display: flex;
      flex-wrap: wrap;
      align-items: center;
      gap: 8px;

      .quick-label {
        font-size: 12px;
        color: #909399;
      }

      .quick-tag {
        cursor: pointer;
        transition: all 0.2s;

        &:hover {
          background: #409eff;
          color: #fff;
          border-color: #409eff;
        }
      }
    }
  }
}
</style>