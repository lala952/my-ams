<template>
  <div class="app-container">
    <!-- 状态分页签 -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane
          v-for="item in statusOptions"
          :key="item.value"
          :name="item.value"
      >
        <template #label>
          <span>{{ item.label }}({{ statusCounts[item.value] || 0 }})</span>
        </template>
      </el-tab-pane>
    </el-tabs>

    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="变动单编码" prop="changeCode">
        <el-input v-model="queryParams.changeCode" placeholder="请输入变动单编码" clearable @keyup.enter="handleQuery"
                  style="width: 180px"/>
      </el-form-item>
      <el-form-item label="变动类型" prop="changeType">
        <el-select v-model="queryParams.changeType" placeholder="请选择" clearable style="width: 180px">
          <el-option v-for="dict in change_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="申请日期" style="width: 308px">
        <el-date-picker v-model="dateRange" value-format="YYYY-MM-DD" type="daterange" range-separator="-"
                        start-placeholder="开始日期" end-placeholder="结束日期"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">清空</el-button>
      </el-form-item>
    </el-form>
    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :disabled="multiple" @click="handleBatchApprove"
                   v-hasPermi="['asset:change:approve']">批量审批
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain @click="handleExport"
                   v-hasPermi="['asset:change:export']">导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="changeList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" :selectable="checkSelectable" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column label="变动单编码" prop="changeCode" show-overflow-tooltip >
        <template #default="scope">
          <a class="link-type" style="cursor:pointer" @click="handleView(scope.row)">{{ scope.row.changeCode }}</a>
        </template>
      </el-table-column>
      <el-table-column label="审批状态" prop="businessStatus" >
        <template #default="scope">
          <ApprovalStatusBubble
              :business-status="scope.row.businessStatus"
              :proc-inst-id="scope.row.procInstId"
              :business-status-dict="business_status"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="变动类型" prop="changeType">
        <template #default="scope">
          <dict-tag :options="change_type" :value="scope.row.changeType"/>
        </template>
      </el-table-column>
      <el-table-column label="申请人" prop="applicantName" show-overflow-tooltip/>
      <el-table-column label="申请部门" prop="applicantDeptName"
                       show-overflow-tooltip/>
      <el-table-column label="申请日期" prop="applyDate">
        <template #default="scope">
          <span>{{ parseTime(scope.row.applyDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="变动原因" prop="changeReason" show-overflow-tooltip/>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>

      <el-table-column label="操作" align="center" fixed="right">
        <template #default="scope">
          <el-button link type="primary" @click="handleApprove(scope.row)"
                     v-if="scope.row.businessStatus === 'pending'" v-hasPermi="['asset:change:approve']">审批
          </el-button>
          <el-button link type="primary" @click="handleView(scope.row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize"
                @pagination="getList"/>

    <!-- 批量审批弹窗 -->
    <el-dialog v-model="batchApproveVisible" title="批量审批" width="500px" append-to-body>
      <el-form :model="batchForm">
        <el-form-item label="审批意见">
          <el-select v-model="batchForm.comment" placeholder="请选择或输入审批意见" allow-create filterable clearable
                     style="width: 100%">
            <el-option label="同意" value="同意"/>
            <el-option label="同意，请按要求执行" value="同意，请按要求执行"/>
            <el-option label="情况属实，同意" value="情况属实，同意"/>
            <el-option label="不符合规定，驳回" value="不符合规定，驳回"/>
            <el-option label="材料不全，请补充" value="材料不全，请补充"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchApproveVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBatchApprove" :loading="batchLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ChangeApproveList">
import {computed, getCurrentInstance, onMounted, reactive, ref, toRefs} from "vue";
import {useRouter} from "vue-router";
import {ElMessage, ElMessageBox} from "element-plus";
import {batchApprove, countByStatus, listChange} from "@/api/asset/change";
import ApprovalStatusBubble from '@/components/Asset/BusinessStatusPopover/index.vue';

const {proxy} = getCurrentInstance();
const router = useRouter();

// 设置分页大小


const {change_type, business_status} = proxy.useDict('change_type', 'business_status');

// 根据字典动态生成Tab选项(排除'草稿'状态)
const statusOptions = computed(() => {
  return business_status.value
      .filter(item => item.value !== 'draft') // 审批页面不显示草稿状态
      .map(item => ({
        label: item.label,
        value: item.value
      }))
})

const changeList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const multiple = ref(true);
const total = ref(0);
const activeTab = ref("pending");
const dateRange = ref([]);
const statusCounts = ref({});
const batchApproveVisible = ref(false);
const batchLoading = ref(false);

const batchForm = reactive({comment: ''});

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    changeCode: null,
    changeType: null,
    businessStatus: null,
  },
});

const {queryParams} = toRefs(data);

function checkSelectable(row) {
  return row.businessStatus === "pending";
}

function getStatusCounts() {
  countByStatus().then((res) => {
    if (res.code === 200 && res.data) {
      statusCounts.value = {
        draft: res.data["draft"] || 0,
        pending: res.data["pending"] || 0,
        rejected: res.data["rejected"] || 0,
        completed: res.data["completed"] || 0,
      };
    }
  });
}

function getList() {
  loading.value = true;
  queryParams.value.businessStatus = activeTab.value;

  listChange(proxy.addDateRange(queryParams.value, dateRange.value))
      .then((res) => {
        loading.value = false;
        changeList.value = res.rows || [];
        total.value = res.total || 0;
      })
      .catch(() => {
        loading.value = false;
      });
}

function handleTabChange(tabName) {
  activeTab.value = tabName;
  queryParams.value.pageNum = 1;
  getList();
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map((item) => item.id);
  multiple.value = !selection.length;
}

function handleApprove(row) {
  router.push({
    path: "/asset/approve/center/change/approve/detail",
    query: {id: row.id},
  });
}

function handleView(row) {
  router.push({
    path: "/asset/approve/center/change/approve/detail",
    query: {id: row.id},
  });
}

function handleBatchApprove() {
  if (ids.value.length === 0) {
    ElMessage.warning("请选择要审批的单据");
    return;
  }
  batchForm.comment = "";
  batchApproveVisible.value = true;
}

function submitBatchApprove() {
  if (!batchForm.comment) {
    ElMessage.warning("请输入审批意见");
    return;
  }

  ElMessageBox.confirm(
      `确认批量审批选中的 ${ids.value.length} 条单据吗？`,
      "提示",
      {confirmButtonText: "确定", cancelButtonText: "取消", type: "warning"}
  )
      .then(() => {
        batchLoading.value = true;
        return batchApprove({
          ids: ids.value,
          remark: batchForm.comment,
          result: "approved",
        });
      })
      .then((res) => {
        if (res.code === 200) {
          ElMessage.success("批量审批成功");
          batchApproveVisible.value = false;
          getList();
          getStatusCounts();
        }
      })
      .catch((err) => {
        if (err !== "cancel") {
          ElMessage.error(err.msg || "批量审批失败");
        }
      })
      .finally(() => {
        batchLoading.value = false;
      });
}

function handleExport() {
  proxy.download(
      "asset/change/export",
      {...queryParams.value},
      `资产变动审批_${new Date().getTime()}.xlsx`
  );
}

function parseTime(time, pattern) {
  return proxy.parseTime(time, pattern);
}

onMounted(() => {
  getList();
  getStatusCounts();
});
</script>

<style scoped lang="scss">
:deep(.el-tabs__header) {
  margin-bottom: 20px;
}
</style>