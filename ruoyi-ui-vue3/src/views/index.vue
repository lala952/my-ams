<template>
  <div class="app-container">
    <div class="header">
      <div class="section">
        <h1 class="title">欢迎使用资产管理系统</h1>
        <p class="description">
          统一资产管理平台，实现资产全生命周期数字化管控
        </p>
      </div>
    </div>

    <div class="body">
      <div class="section">
        <h3 class="section-title">资产台账管理</h3>
        <p class="description">
          建立完整的资产电子档案，支持资产信息录入、编辑、查询、导出，实现资产全生命周期追踪管理
        </p>
      </div>

      <div class="section">
        <h3 class="section-title">资产变动管理</h3>
        <p class="description">
          支持资产信息变更、价值调整等业务，审批流程自动化，确保资产变动有据可查
        </p>
      </div>

      <div class="section">
        <h3 class="section-title">资产盘点管理</h3>
        <p class="description">
          支持固定资产定期盘点，生成盘点差异报告，确保账实相符，盘盈盘亏有据可依
        </p>
      </div>

      <div class="section">
        <h3 class="section-title">资产维修保养</h3>
        <p class="description">
          记录资产维修保养历史，设定保养周期提醒，延长资产使用寿命
        </p>
      </div>

      <div class="section">
        <h3 class="section-title">资产二维码管理</h3>
        <p class="description">
          为每项资产生成唯一二维码标签，手机扫码即可查看资产详细信息，支持批量打印、一键导出，实现资产快速识别与精准管理，提升资产盘点效率
        </p>
      </div>
    </div>

    <div class="footer"></div>
  </div>
</template>

<script setup>
import {getCurrentInstance, onMounted, ref} from "vue";
import {ElMessage} from "element-plus";
import {useRouter} from "vue-router";
import CurrentTodo from "@/components/Asset/CurrentTodo"

const router = useRouter();
const {proxy} = getCurrentInstance();


onMounted(() => {
  proxy.$modal.notify("为了获得更好的视觉体验，建议您将页面缩放至 90%（Ctrl + - 或 Cmd + - 缩小，Ctrl + 0 恢复默认）")
});
</script>

<style scoped lang="scss">
.dashboard-container {
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.body {
  flex: 1;
}

.section {
  margin-bottom: 10px;
  color: var(--el-text-color-primary);

  .description {
    padding: 0 15px;
    color: var(--el-text-color-primary);
    line-height: 1.6;
  }
}

.footer {
  margin-top: 25px;
  padding: 5px 5px 0;
}
</style>
