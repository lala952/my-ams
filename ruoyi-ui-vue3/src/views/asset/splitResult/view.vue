<template>
  <el-drawer title="资产拆分结果详情" v-model="visible" direction="rtl" size="60%" append-to-body
             :before-close="handleClose" class="detail-drawer">
    <div v-loading="loading" class="drawer-content">
      <h4 class="section-header">基本信息</h4>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">拆分单ID：</label>
            <span class="info-value plaintext">{{ info.splitId }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">新资产编码：</label>
            <span class="info-value plaintext">{{ info.newAssetCode }}</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">新资产名称：</label>
            <span class="info-value plaintext">{{ info.newAssetName }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">拆分数量：</label>
            <span class="info-value plaintext">{{ info.splitQuantity }}</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">分摊原值：</label>
            <span class="info-value plaintext">{{ info.originalValue }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">使用人ID：</label>
            <span class="info-value plaintext">{{ info.userId }}</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">使用部门ID：</label>
            <span class="info-value plaintext">{{ info.deptId }}</span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">存放位置：</label>
            <span class="info-value plaintext">{{ info.location }}</span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">执行状态：</label>
            <span class="info-value plaintext">
              <dict-tag :options="execute_status" :value="info.executeStatus"/>
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">生成的资产ID：</label>
            <span class="info-value plaintext">{{ info.targetAssetId }}</span>
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

<script setup name="SplitResultViewDrawer">
import { getSplitResult } from '@/api/asset/splitResult'

const { execute_status } = useDict('execute_status')

const visible = ref(false)
const loading = ref(false)
const info = reactive({})

const open = async (id) => {
  visible.value = true
  loading.value = true
  try {
    const res = await getSplitResult(id)
    Object.assign(info, res.data || {})
  } catch (error) {
    console.error('获取资产拆分结果信息失败:', error)
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