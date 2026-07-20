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
          :show-toolbar="true"
          :show-total-value="true"
          :total="assets.length"
          :disabledEditAndRemove="detail.businessStatus == 'completed' ? true : false"
          mode="view"
      />
    </div>

    <!-- 底部按钮 -->
    <div class="footer-button-actions">
      <el-button type="danger" @click="handleWithdraw" v-hasPermi="['asset:change:withdraw']">撤回</el-button>
      <el-button type="primary" @click="handleExportPdf" v-hasPermi="['asset:change:export']">打印</el-button>
    </div>

    <!-- 右侧面板 -->
    <ChangeSidePanel
        v-model="showPanel"
        :attachments="allAttachments"
        :proc-inst-id="detail.procInstId"
        :business-code="detail.changeCode"
        title="附件列表与审批轨迹"
    />
  </div>
</template>

<script name="ChangeQueryDetail" setup>
import {getCurrentInstance, onMounted, ref} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {exportChangePdf, getChange, withdrawChange} from '@/api/asset/change'
import ChangeSidePanel from '@/components/Asset/SidePanel'
import AssetLedgerPanel from "@/components/Asset/AssetLedgerPanel"

const {proxy} = getCurrentInstance()
const route = useRoute()
const router = useRouter()

// 只需要保留页面级需要的字典
const {change_type} = proxy.useDict('change_type')

const detail = ref({})
const assets = ref([])
const allAttachments = ref([])
const showPanel = ref(false)

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
      //  直接赋值，AssetLedgerPanel 内部会自动处理字典转换和显示
      assets.value = res.data.assets || []
      allAttachments.value = res.data.attachments || []
    }
  } catch (error) {
    proxy.$modal.msgError('加载详情失败')
    router.back()
  }
}

function handleWithdraw() {
  proxy.$modal.confirm('确认撤回该申请吗？').then(async () => {
    try {
      await withdrawChange(detail.value.id)
      proxy.$modal.msgSuccess('撤回成功')
      router.push('/asset/query/center/change/query')
    } catch (error) {
    }
  })
}

function handleExportPdf() {
  exportChangePdf(detail.value.id).then(blob => {
    const url = window.URL.createObjectURL(new Blob([blob], {type: 'application/pdf'}))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `资产变动单_${detail.value.changeCode}.pdf`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    proxy.$modal.msgSuccess('PDF导出成功')
  }).catch(() => {
    proxy.$modal.msgError('PDF导出失败')
  })
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