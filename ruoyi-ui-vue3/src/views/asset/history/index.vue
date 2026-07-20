<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="资产名称" prop="assetName">
        <el-input
            v-model="queryParams.assetName"
            placeholder="请输入资产名称"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="事件类型" prop="eventType">
        <el-select v-model="queryParams.eventType" placeholder="请选择事件类型" clearable style="width: 150px">
          <el-option
              v-for="dict in history_event_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="事件名称" prop="eventName">
        <el-input
            v-model="queryParams.eventName"
            placeholder="请输入事件名称"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="操作人" prop="operatorName">
        <el-input
            v-model="queryParams.operatorName"
            placeholder="请输入操作人姓名"
            clearable
            @keyup.enter="handleQuery"
            style="width: 150px"
        />
      </el-form-item>
      <el-form-item label="操作时间" prop="operateTime">
        <el-date-picker clearable
                        v-model="queryParams.operateTime"
                        type="date"
                        value-format="YYYY-MM-DD"
                        placeholder="请选择操作时间"
                        style="width: 180px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">清空</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete" v-hasPermi="['asset:history:remove']">
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain @click="handleExport" v-hasPermi="['asset:history:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="historyList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column align="center" label="记录ID" prop="id" show-overflow-tooltip sortable width="80"/>
      <el-table-column align="center" label="资产编码" prop="assetCode" show-overflow-tooltip sortable
      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.assetCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column label="资产名称" prop="assetName" show-overflow-tooltip sortable
      />
      <el-table-column align="center" label="事件类型" prop="eventType" sortable>
        <template #default="scope">
          <dict-tag :options="history_event_type" :value="scope.row.eventType"/>
        </template>
      </el-table-column>
      <el-table-column label="事件名称" prop="eventName" show-overflow-tooltip sortable
      />
      <el-table-column label="事件详情" min-width="200" prop="eventDetail" show-overflow-tooltip sortable
      >
        <template #default="scope">
          <span>{{ scope.row.eventDetail || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人" prop="operatorName" show-overflow-tooltip sortable width="100"
      />
      <el-table-column align="center" label="操作时间" prop="operateTime" sortable width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.operateTime) || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="IP地址" prop="ipAddress" sortable width="140" show-overflow-tooltip align="center"
      />
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['asset:history:query']">详情
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:history:remove']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 资产履历详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="资产履历详情" size="50%" direction="rtl">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="记录ID">{{ viewData.id }}</el-descriptions-item>
        <el-descriptions-item label="资产编码">{{ viewData.assetCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ viewData.assetName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="事件类型">
          <dict-tag :options="history_event_type" :value="viewData.eventType"/>
        </el-descriptions-item>
        <el-descriptions-item label="事件名称">{{ viewData.eventName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="事件详情">{{ viewData.eventDetail || '-' }}</el-descriptions-item>
        <el-descriptions-item label="变更前数据">{{ viewData.beforeData || '-' }}</el-descriptions-item>
        <el-descriptions-item label="变更后数据">{{ viewData.afterData || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ viewData.operatorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ parseTime(viewData.operateTime) || '-' }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ viewData.ipAddress || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup lang="ts" name="History">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import type {History, HistoryQueryParams} from "@/types/api/asset/history"
import {delHistory, listHistory} from "@/api/asset/history"
import {parseTime} from '@/utils/ruoyi'

const {proxy} = getCurrentInstance()
const {history_event_type} = proxy.useDict('history_event_type')

const historyList = ref<History[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const ids = ref<number[]>([])
const single = ref<boolean>(true)
const multiple = ref<boolean>(true)
const total = ref<number>(0)
const title = ref<string>("")
const drawerVisible = ref<boolean>(false)
const viewData = ref<History>({} as History)

const data = reactive({
  form: {} as History,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    assetName: undefined,
    eventType: undefined,
    eventName: undefined,
    operatorName: undefined,
    operateTime: undefined,
  } as HistoryQueryParams,
  rules: {}
})

const {queryParams, form, rules} = toRefs(data)

function getList() {
  loading.value = true
  listHistory(queryParams.value).then(response => {
    historyList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection: History[]) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: null,
    assetId: null,
    eventType: null,
    eventName: null,
    eventDetail: null,
    beforeData: null,
    afterData: null,
    operatorId: null,
    operatorName: null,
    operateTime: null,
    ipAddress: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  }
  proxy.resetForm("historyRef")
}

function handleView(row: History) {
  viewData.value = row
  drawerVisible.value = true
}

function handleDelete(row: History) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除资产履历编号为"' + _ids + '"的数据项？').then(function () {
    return delHistory(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleExport() {
  proxy.download('asset/history/export', {
    ...queryParams.value
  }, `资产履历_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList()
})
</script>
