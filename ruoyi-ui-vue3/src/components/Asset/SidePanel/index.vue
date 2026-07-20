<template>
  <div class="app-container">
    <!-- 触发按钮 -->
    <div class="drawer-trigger" @click="handleToggle">
      <div v-if="showAttachmentText" class="trigger-text">附件材料</div>
      <div v-if="showTimelineText" class="trigger-text">审批轨迹</div>
      <el-icon style="text-align: left; vertical-align: text-top; font-size: 12px">
        <ArrowRightBold v-if="visible"/>
        <ArrowLeftBold v-else/>
      </el-icon>
    </div>

    <!-- 右侧面板 - 使用 el-dialog -->
    <el-dialog
        v-model="visible"
        :width="dialogWidth"
        append-to-body
        :z-index="998"
    >
      <!-- Tab切换 -->
      <el-tabs v-model="activeTab">
        <!-- 附件材料 Tab -->
        <el-tab-pane label="附件材料" name="attachment" v-if="showAttachmentTab">
          <div style="display: flex;justify-content: space-between">
            <TreePanel
                ref="attachmentTreeRef"
                title="附件列表"
                :tree-data="attachmentTreeData"
                :tree-props="{ children: 'children', label: 'label' }"
                node-key="id"
                :default-expand-all="true"
                :show-search="true"
                search-placeholder="请输入关键词搜索"
                storage-key="attachment-sidebar-width"
                :default-width="350"
                :min-width="250"
                :max-width="600"
                @node-click="handleAttachmentNodeClick"
                @refresh="handleRefresh"
            >
              <template #node="{ node, data }">
                <p class="node-label" :title="node.label">
                  <el-icon>
                    <Folder v-if="data.type === 'category'"/>
                    <Document v-else/>
                  </el-icon>
                  {{ node.label }}({{ data.count }})
                </p>
              </template>
            </TreePanel>
            <!-- 右侧文件预览区域 -->
            <div class="preview-area">
              <div class="preview-inner">
                <el-empty v-if="!selectedFile" size="60" description="请选择文件查看"></el-empty>
                <el-card shadow="never" v-else>
                  <!-- 文件头部 -->
                  <div style="display: flex; align-items: center; gap: 16px;padding: 10px 0;">
                    <el-icon :size="40" color="var(--el-color-primary)">
                      <Document/>
                    </el-icon>
                    <div style="flex: 1; min-width: 0">
                      <el-tooltip :content="selectedFile.attachmentName">
                        <div
                            style="font-size: 16px; font-weight: 600;  margin-bottom: 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap">
                          {{ selectedFile.attachmentName }}
                        </div>
                      </el-tooltip>
                      <div style="display: flex; gap: 12px; font-size: 12px;">
                        <dict-tag :options="attachment_type" :value="selectedFile.attachmentType"/>
                      </div>
                    </div>
                  </div>
                  <!-- 文件详情 -->
                  <el-descriptions :column="2" border>
                    <el-descriptions-item label="上传者" label-width="80">
                      {{ selectedFile.uploadBy || '-' }}
                    </el-descriptions-item>
                    <el-descriptions-item label="上传时间" label-width="80">
                      {{ selectedFile.uploadTime || '-' }}
                    </el-descriptions-item>
                  </el-descriptions>

                  <!-- 操作按钮 -->
                  <div class="footer-button-actions">
                    <el-button type="primary" @click="downloadFile(selectedFile)">下载文件</el-button>
                    <el-button @click="previewFile(selectedFile)">预览文件</el-button>
                  </div>
                </el-card>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 审批轨迹 Tab -->
        <el-tab-pane label="审批轨迹" name="timeline" v-if="showTimelineTab">
          <h4 v-if="businessCode" class="business-code" style="font-weight: bold">
            <span>业务单据编码：</span>
            <span style="color:var(--el-color-warning)">{{ businessCode }}</span>
          </h4>
          <ApprovalSteps :procInstId="procInstId"/>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup name="ChangeSidePanel">
import {computed, getCurrentInstance, onMounted, ref, watch} from 'vue'
import {Document, Folder} from '@element-plus/icons-vue'
import TreePanel from '@/components/TreePanel/index.vue'
import ApprovalSteps from '@/components/Asset/ApprovalSteps'

const {proxy} = getCurrentInstance()
const {attachment_type} = proxy.useDict("attachment_type")

const props = defineProps({
  modelValue: {type: Boolean, default: false},
  title: {type: String, default: '附报材料与审批轨迹'},
  width: {type: [String, Number], default: '70%'},
  showAttachmentText: {type: Boolean, default: true},
  showTimelineText: {type: Boolean, default: true},
  attachments: {type: Array, default: () => []},
  procInstId: {type: String, default: ''},
  businessCode: {type: String, default: ''},
  showAttachmentTab: {type: Boolean, default: true},
  showTimelineTab: {type: Boolean, default: true}
})

const emit = defineEmits(['update:modelValue', 'open', 'close', 'toggle', 'refresh'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const dialogWidth = computed(() => {
  return typeof props.width === 'number' ? `${props.width}px` : props.width
})

const activeTab = ref('attachment')
const attachmentTreeData = ref([])
const selectedFile = ref(null)

const applyAttachments = computed(() => {
  return (props.attachments || []).filter(att => att.attachmentType === 'APPLY')
})

const identifyAttachments = computed(() => {
  return (props.attachments || []).filter(att => att.attachmentType === 'IDENTIFY')
})

function buildAttachmentTree() {
  const treeData = []

  treeData.push({
    id: 'apply_category',
    label: '申请文件',
    type: 'category',
    count: applyAttachments.value.length,
    children: applyAttachments.value.map((file, index) => ({
      id: `apply_${file.id || index}`,
      label: file.attachmentName,
      type: 'file',
      fileData: file
    }))
  })

  treeData.push({
    id: 'identify_category',
    label: '鉴定材料',
    type: 'category',
    count: identifyAttachments.value.length,
    children: identifyAttachments.value.map((file, index) => ({
      id: `identify_${file.id || index}`,
      label: file.attachmentName,
      type: 'file',
      fileData: file
    }))
  })

  attachmentTreeData.value = treeData
}

function handleAttachmentNodeClick(data) {
  if (data.type === 'file' && data.fileData) {
    selectedFile.value = data.fileData
  } else {
    selectedFile.value = null
  }
}

function downloadFile(attachment) {
  if (attachment.filePath) {
    window.open(attachment.filePath, '_blank')
  }
}

function previewFile(attachment) {
  if (attachment.filePath) {
    window.open(attachment.filePath, '_blank')
  }
}

function handleRefresh() {
  emit('refresh')
}

function handleToggle() {
  visible.value = !visible.value
  emit('toggle', visible.value)
  if (visible.value) {
    emit('open')
  } else {
    emit('close')
  }
}

function close() {
  visible.value = false
  emit('close')
}

function open() {
  visible.value = true
  emit('open')
}

defineExpose({open, close, toggle: handleToggle})

watch(() => props.attachments, () => {
  buildAttachmentTree()
}, {immediate: true, deep: true})

onMounted(() => {
  buildAttachmentTree()
})
</script>
<style scoped lang="scss">

.drawer-trigger {
  position: fixed;
  right: 0;
  top: 20%;
  width: 20px;
  height: 160px;
  transform: translateY(-50%);
  background: var(--el-color-warning);
  border-radius: 5px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding-left: 4px;
  cursor: pointer;
  z-index: 999;
  color: var(--el-text-color-primary);
  gap: 8px;
  transition: all 0.3s ease;

  &:hover {
    background: var(--el-color-warning-dark-2);
    width: 20px;
  }

  .trigger-text {
    font-size: 12px;
    font-weight: 600;
  }
}

:deep(.el-overlay) {
  z-index: 997 !important;
}

.preview-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: auto;
  background-color: var(--el-bg-color);
}

.preview-inner {
  flex: 1;
  padding: 0 5px;
  overflow: auto;
}

.timeline-panel {
  height: 100%;
  overflow: auto;
  padding: 12px 20px;
}

.business-code {
  padding: 0 0 12px 0;
  margin: 0;
}

.node-label {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: 1;
  font-size: 13px;
  margin: 0;

  .el-icon {
    font-size: 14px;
    color: var(--el-color-primary);
    flex-shrink: 0;
  }
}

@media (max-width: 768px) {
  :deep(.change-side-panel-dialog) {
    width: 100% !important;

    .el-dialog {
      width: 100% !important;
    }
  }
}
</style>