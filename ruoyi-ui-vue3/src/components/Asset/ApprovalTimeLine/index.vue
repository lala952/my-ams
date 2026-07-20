<template>
  <div style="padding: 3px;" v-loading="loading" element-loading-text="加载中……">
    <el-empty v-if="!historyList || historyList.length === 0" :image-size="80" description="暂无审批记录"/>
    <el-timeline v-else style="padding: 0px" reverse="reverse">
      <el-timeline-item
          v-for="(item, index) in historyList"
          :key="index"
          :color="item.approved ? 'var(--el-color-success)' : 'var(--el-color-danger)'"
          :icon="item.approved ? 'CircleCheck' : 'CircleClose'"
          size="large"
          style="padding: 0px"
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
          <el-divider direction="vertical"/>持续时间：{{ formatDuration(item.durationInMillis) }}
        </p>
        <p v-if="item.comment">
          审批意见：<span :style="{ color: item.approved ? 'var(--el-color-primary)' : 'var(--el-color-danger)' }">{{
            item.comment
          }}</span>
        </p>
        <p v-if="item.taskName">
          环节名称：<span>{{ item.taskName }}</span>
        </p>
      </el-timeline-item>
    </el-timeline>
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
      historyList.value = res.data
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