<template>
  <el-drawer title="资产拆分明细详情" v-model="visible" direction="rtl" size="60%" append-to-body
             :before-close="handleClose" class="detail-drawer">
    <div v-loading="loading" class="drawer-content">
      <h4 class="section-header">基本信息</h4>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">拆分单编码：</label>
            <span class="info-value plaintext">{{ info.splitCode }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">原资产ID：</label>
            <span class="info-value plaintext">{{ info.sourceAssetId }}</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">拆分原因：</label>
            <span class="info-value plaintext">{{ info.splitReason }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">拆分日期：</label>
            <span class="info-value plaintext">{{ parseTime(info.splitDate, '{y}-{m}-{d}') }}</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">拆分总数：</label>
            <span class="info-value plaintext">{{ info.splitQuantity }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">业务状态：</label>
            <span class="info-value plaintext">
              <dict-tag :options="business_status" :value="info.businessStatus"/>
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">审批人ID：</label>
            <span class="info-value plaintext">{{ info.approverId }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">审批时间：</label>
            <span class="info-value plaintext">{{ parseTime(info.approveTime, '{y}-{m}-{d}') }}</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">备注：</label>
            <span class="info-value plaintext">{{ info.remark }}</span>
          </div>
        </el-col>
      </el-row>
    </div>
  </el-drawer>
</template>

<script setup name="SplitViewDrawer">
import { getSplit } from '@/api/asset/split'

const { business_status } = useDict('business_status')

const visible = ref(false)
const loading = ref(false)
const info = reactive({})

const open = async (id) => {
  visible.value = true
  loading.value = true
  try {
    const res = await getSplit(id)
    Object.assign(info, res.data || {})
  } catch (error) {
    console.error('获取资产拆分明细信息失败:', error)
  } finally {
    loading.value = false
  }
}

function handleClose() {
  visible.value = false
  Object.keys(info).forEach(key => delete info[key])
}

defineExpose({ open })
</script>