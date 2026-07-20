<template>
  <div v-loading="loading" element-loading-text="加载中……">
    <el-empty v-if="!historyList || historyList.length === 0" :image-size="80" description="暂无审批记录"/>
    <div v-else class="cards-track">
      <el-card
          v-for="(item, index) in historyList"
          :key="index"
          class="approval-card"
          shadow="never"
      >
        <p style="display: flex; align-items: center; justify-content: space-between">
            <span style="font-weight: bold;">
              <el-icon style="vertical-align: text-top;margin-right: 5px;"><User/></el-icon>
              {{ item.assigneeName }}
            </span>
          <dict-tag v-if="item.taskDefinitionKey" :options="act_task_definition_key" :value="item.taskDefinitionKey"/>
          <dict-tag v-if="item.nodeStatus" :options="act_node_status" :value="item.nodeStatus"/>
        </p>
        <p style="color:var(--el-color-info)">{{ item.endTime || item.startTime }}
          <el-divider direction="vertical"/>
          <span v-if="item.durationInMillis">持续时间：{{ formatDuration(item.durationInMillis) }}</span></p>
        <p v-if="item.comment">
          审批意见：<span :style="{ color: item.approved ? 'var(--el-color-primary)' : 'var(--el-color-danger)' }">{{
            item.comment
          }}</span>
        </p>
        <p v-if="item.taskName">
          环节名称：<span>{{ item.taskName }}</span>
        </p>
      </el-card>
    </div>
  </div>
</template>

<script setup name="HistoryTimeLine">
import {getCurrentInstance, ref, watch} from 'vue'
import {User} from '@element-plus/icons-vue'
import {getApprovalHistory} from '@/api/asset/change'
import {formatDuration} from "../../../utils/asset/index.js";

const {proxy} = getCurrentInstance()
const {act_task_definition_key, act_node_status} = proxy.useDict('act_task_definition_key', 'act_node_status')

const props = defineProps({
  procInstId: {type: String, default: ''}
})

const loading = ref(false)
const historyList = ref([])


async function loadHistory() {
  if (!props.procInstId) return

  loading.value = true
  try {
    const res = await getApprovalHistory(props.procInstId)
    if (res.code === 200 && res.data) {
      historyList.value = res.data.sort(
          (a, b) => new Date(a.startTime) - new Date(b.startTime)
      )
    }
  } catch (error) {
    console.error('获取审批历史失败:', error)
  } finally {
    loading.value = false
  }
}

watch(() => props.procInstId, () => {
  historyList.value = []
  loadHistory()
}, {immediate: true})

defineExpose({refresh: loadHistory})
</script>

<style scoped lang="scss">
.cards-track {
  display: flex;
  gap: 10px;
  overflow-x: auto;
  &::-webkit-scrollbar {
    width: 3px;
    height: 3px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(0, 0, 0, 0.15);
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb:hover {
    background: rgba(0, 0, 0, 0.25);
  }

  &::-webkit-scrollbar-corner {
    background: transparent;
  }
}

.approval-card {
  min-width: 300px;
  border-top: 3px solid var(--el-color-primary);
  flex-shrink: 0;

  p {
    margin: 8px 0;
  }
}
</style>