<template>
  <div class="app-container">
    <div class="main">
      <!-- 变动单类型 -->
      <h4 class="section-title">变动单类型</h4>
      <el-form ref="changeRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="变动类型" prop="changeType">
          <el-select v-model="form.changeType" clearable placeholder="请选择变动类型" style="width: 50%">
            <el-option v-for="dict in change_type || []" :key="dict.value" :label="dict.label" :value="dict.value"/>
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 基本信息 -->
      <h4 class="section-title">基本信息</h4>
      <el-form ref="changeRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item label="变动单编码">
              <el-input v-model="form.changeCode" disabled/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="申请人">
              <el-input :value="applicantInfo?.nickName || '-'" disabled/>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="申请日期">
              <el-date-picker v-model="form.applyDate" disabled style="width: 100%" type="date" value-format="YYYY-MM-DD"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="变动原因" prop="changeReason">
              <el-input v-model="form.changeReason" :rows="1" maxlength="500" placeholder="请输入变动原因" show-word-limit type="textarea"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" :rows="1" maxlength="500" placeholder="请输入备注" show-word-limit type="textarea"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <!-- 资产清单（内置资产选择器） -->
      <h4 class="section-title">资产清单</h4>
      <AssetLedgerPanel
          v-model="form.assets"
          :total="form.assets.length"
          :editable="true"
          :searchable="true"
          :show-total-value="true"
          :enable-asset-selector="true"
          :asset-selector-filter="{ assetStatus: 'in_use' }"
      />

      <!-- 附件材料 -->
      <h4 class="section-title">附件材料</h4>
      <el-tabs v-model="activeTab">
        <el-tab-pane v-for="item in attachment_type" :key="item.value" :name="item.value">
          <template #label>
            <span>{{ item.label }}({{ getAttachmentCount(item.value) }})</span>
          </template>
          <CustomFileUpload
              v-model="attachmentsMap[item.value]"
              :disabled="item.disabled"
              :file-size="item.fileSize"
              :file-type="item.fileType"
              :limit="item.limit"
          />
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 底部按钮 -->
    <div class="footer-button-actions mt20">
      <el-button @click="handleBack">退出</el-button>
      <el-button v-hasPermi="['asset:change:add']" :loading="saveLoading" type="primary" @click="handleSaveDraft">暂存</el-button>
      <el-button v-hasPermi="['asset:change:submit']" :loading="submitLoading" type="primary" @click="handleSubmit">提交</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed, getCurrentInstance, onMounted, reactive, ref, toRefs } from "vue";
import { useRoute, useRouter } from "vue-router";
import useUserStore from "@/store/modules/user";
import {
  getChange,
  saveDraft as saveDraftApi,
  submitChange as submitChangeApi,
} from "@/api/asset/change";
import AssetLedgerPanel from "@/components/Asset/AssetLedgerPanel/index.vue";
import { useAssetOptions } from "@/composables/asset/useAssetOptions";
import CustomFileUpload from "@/components/Asset/CustomFileUpload";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const { proxy } = getCurrentInstance();
const { change_type, attachment_type } = proxy.useDict("change_type", "attachment_type");

const currentUserId = computed(() => userStore.id);
const currentNickName = computed(() => userStore.nickName || userStore.name);

const applicantInfo = ref({ nickName: currentNickName.value });

const saveLoading = ref(false);
const submitLoading = ref(false);
const isSubmitting = ref(false);

const data = reactive({
  form: {
    id: null,
    changeCode: null,
    changeType: null,
    changeReason: null,
    applyDate: new Date().toISOString().slice(0, 10),
    applicantId: currentUserId.value,
    remark: null,
    assets: [],
    attachments: [],
  },
  rules: {
    changeType: [{ required: true, message: "请选择变动类型", trigger: "blur" }],
    changeReason: [
      { required: true, message: "请输入变动原因", trigger: "blur" },
      { min: 5, message: "变动原因至少5个字符", trigger: "blur" },
      { max: 500, message: "变动原因不能超过500个字符", trigger: "blur" },
    ],
  }
});
const { form, rules } = toRefs(data);

// 附件处理
const activeTab = computed(() => {
  const types = attachment_type.value;
  return types && types.length > 0 ? types[0].value : "";
});

const attachmentsMap = ref({});

function getAttachmentCount(type) {
  const value = attachmentsMap.value[type];
  if (!value) return 0;
  return typeof value === "string" ? value.split(",").filter((url) => url).length : 0;
}

function buildAllAttachments() {
  if (!attachment_type.value || attachment_type.value.length === 0) return [];

  const allAttachments = [];
  attachment_type.value.forEach((type) => {
    const value = attachmentsMap.value[type.value];
    if (!value) return;

    const urls = typeof value === "string" ? value.split(",").filter((url) => url) : [];
    urls.forEach((url) => {
      allAttachments.push({
        attachmentName: decodeURIComponent(url.split("/").pop() || "未知文件"),
        filePath: url,
        attachmentType: type.value,
      });
    });
  });
  return allAttachments;
}

// 使用 Composable 加载公共选项
const { loadApplicantInfo, loadAll: loadOptions } = useAssetOptions();

// 加载已有数据
async function loadEditData(id) {
  try {
    const res = await getChange(id);
    if (res.code === 200 && res.data) {
      const data = res.data;
      Object.assign(form.value, {
        id: data.id,
        changeCode: data.changeCode,
        changeType: data.changeType,
        changeReason: data.changeReason,
        applyDate: data.applyDate,
        remark: data.remark,
        applicantId: data.applicantId,
        assets: data.assets || [],
      });

      // 处理附件回显
      const allAttachments = data.attachments || [];
      if (attachment_type.value && attachment_type.value.length > 0) {
        attachment_type.value.forEach((type) => {
          const urlStrings = allAttachments
              .filter((a) => a.attachmentType === type.value)
              .map((att) => att.filePath)
              .join(",");
          attachmentsMap.value[type.value] = urlStrings;
        });
      }

      if (data.applicantId) {
        await loadApplicantInfo(data.applicantId);
      }
    }
  } catch (error) {
    proxy.$modal.msgError("加载数据失败");
  }
}

// 暂存
async function handleSaveDraft() {
  proxy.$refs["changeRef"].validate(async (valid) => {
    if (!valid) return;

    saveLoading.value = true;
    try {
      const draftData = {
        ...form.value,
        attachments: buildAllAttachments(),
      };
      const res = await saveDraftApi(draftData);
      if (res.code === 200) {
        form.value.id = res.data;
        proxy.$modal.msgSuccess("暂存成功");
        router.push("/asset/query/center/change/query");
      }
    } catch (error) {
      proxy.$modal.msgError("暂存失败: " + error.message);
    } finally {
      saveLoading.value = false;
    }
  });
}

// 提交
async function handleSubmit() {
  if (isSubmitting.value) {
    proxy.$modal.msgWarning("正在提交中，请勿重复点击");
    return;
  }

  proxy.$refs["changeRef"].validate(async (valid) => {
    if (!valid) return;

    if (form.value.assets.length === 0) {
      proxy.$modal.msgWarning("请至少选择一个资产");
      return;
    }

    try {
      await proxy.$modal.confirm("确认提交该资产变动申请吗？提交后将进入审批流程");
      isSubmitting.value = true;
      submitLoading.value = true;

      const submitData = {
        ...form.value,
        attachments: buildAllAttachments(),
      };
      const res = await submitChangeApi(submitData);
      if (res.code === 200) {
        proxy.$modal.msgSuccess("提交成功");
        router.push("/asset/query/center/change/query");
      }
    } catch (error) {
      if (error !== "cancel") {
        proxy.$modal.msgError("提交失败");
      }
    } finally {
      isSubmitting.value = false;
      submitLoading.value = false;
    }
  });
}

function handleBack() {
  router.back();
}

onMounted(async () => {
  await loadOptions();
  if (route.query.id) {
    await loadEditData(route.query.id);
  }
});
</script>

<style scoped lang="scss">
</style>