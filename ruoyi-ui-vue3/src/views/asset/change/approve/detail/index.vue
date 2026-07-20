<template>
  <div class="app-container change-apply-container">
    <div class="main">
      <!-- 变动单类型 -->
      <h4 class="section-title">变动单类型</h4>
      <el-form :model="detail" label-width="120px">
        <el-form-item label="变动类型">
          <el-select v-model="detail.changeType" disabled style="width: 50%">
            <el-option v-for="dict in change_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 基本信息 -->
      <h4 class="section-title">基本信息</h4>
      <el-form :model="detail" label-width="120px">
        <el-row>
          <el-col :span="8">
            <el-form-item label="变动单编码">
              <el-input v-model="detail.changeCode" disabled/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="资产总数量">
              <el-input :value="assets.length" disabled style="text-align: right"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="申请人">
              <el-input :value="detail.applicantName" disabled/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="申请部门">
              <el-input :value="detail.applicantDeptName" disabled/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="申请日期">
              <el-date-picker v-model="detail.applyDate" disabled style="width: 100%" type="date"
                              value-format="YYYY-MM-DD"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="变动原因">
              <el-input v-model="detail.changeReason" :rows="1" disabled type="textarea"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="detail.remark" :rows="1" disabled type="textarea"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 资产清单 ( 极简：使用增强版 AssetLedgerPanel) -->
      <h4 class="section-title">资产清单</h4>
      <AssetLedgerPanel
          v-model="assets"
          :editable="false"
          :searchable="true"
          :show-total-value="true"
          :total="assets.length"
          mode="view"
      />
    </div>

    <!-- 审批操作区 -->
    <el-form>
      <el-form-item label="审批意见">
        <div style="display: flex; align-items: center; gap: 10px; width: 100%">
          <el-select
              v-model="approvalForm.comment"
              allow-create
              clearable
              filterable
              placeholder="请直接输入您的意见或下拉选择常用意见"
              size="default"
              style="flex: 1"
          >
            <el-option v-for="comment in commonComments" :key="comment" :label="comment" :value="comment"/>
          </el-select>
          <el-button type="primary" @click="handleApprove(true)">通过</el-button>
          <el-button type="danger" @click="handleApprove(false)">驳回</el-button>
        </div>
      </el-form-item>
    </el-form>

    <!-- 右侧面板 -->
    <ChangeSidePanel
        v-model="showPanel"
        :attachments="allAttachments"
        :proc-inst-id="detail.procInstId"
        :business-code="detail.changeCode"
    />
  </div>
</template>

<script setup name="ChangeApproveDetail">
import {getCurrentInstance, onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {approveChange, getChange} from '@/api/asset/change'
import AssetLedgerPanel from '@/components/Asset/AssetLedgerPanel'
import ChangeSidePanel from '@/components/Asset/SidePanel'

const {proxy} = getCurrentInstance()
const route = useRoute()
const router = useRouter()

//  引入 attachment_type 字典，实现动态处理
const {change_type, attachment_type} = proxy.useDict('change_type', 'attachment_type')

const detail = ref({})
const assets = ref([])
const allAttachments = ref([])
const approvalForm = ref({comment: ''})
const showPanel = ref(false)

const commonComments = [
  '同意', '同意，请按规定办理', '情况属实，同意变更', '符合规定，予以批准',
  '不符合规定，驳回', '材料不全，请补充后重新提交', '资产信息有误，请核实'
]

async function loadDetail() {
  const id = route.query.id
  if (!id) {
    proxy.$modal.msgError('缺少变动单ID')
    router.back()
    return
  }

  try {
    const res = await getChange(id)
    if (res.code === 200 && res.data) {
      detail.value = res.data
      //  直接赋值，AssetLedgerPanel 内部会自动处理字典转换
      assets.value = Array.isArray(res.data.assets) ? res.data.assets : []

      //  动态赋值所有附件，不再写死 APPLY 或 IDENTIFY
      allAttachments.value = res.data.attachments || []
    }
  } catch (error) {
    proxy.$modal.msgError('加载详情失败')
    router.back()
  }
}

async function handleApprove(approved) {
  if (!approvalForm.value.comment) {
    proxy.$modal.msgWarning('请填写审批意见')
    return
  }

  await proxy.$modal.confirm(`确定要${approved ? '通过' : '驳回'}该申请吗？`)
  await approveChange({
    id: detail.value.id,
    result: approved,
    comment: approvalForm.value.comment
  })

  proxy.$modal.msgSuccess('审批成功')
  await router.push('/asset/approve/center/change/approve')
}

onMounted(async () => {
  await loadDetail()
})
</script>

<style lang="scss" scoped>
.main {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--el-border-color);
}
</style>