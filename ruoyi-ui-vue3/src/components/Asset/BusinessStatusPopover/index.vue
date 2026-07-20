<template>
  <el-popover
      :width="350"
      trigger="hover"
  >
    <template #reference>
      <dict-tag v-if="businessStatus" :options="business_status" :value="businessStatus"/>
    </template>

    <el-scrollbar max-height="180px" style="padding:0">
      <HistoryTimeLine :procInstId="procInstId"/>
    </el-scrollbar>
  </el-popover>
</template>

<script setup name="ApprovalStatusBubble">
import { getCurrentInstance } from 'vue'
import HistoryTimeLine from "@/components/Asset/ApprovalTimeLine"

const { proxy } = getCurrentInstance()
const { business_status } = proxy.useDict('business_status')

const props = defineProps({
  businessStatus: { type: String, default: '' },
  procInstId: { type: String, default: '' }
})
</script>