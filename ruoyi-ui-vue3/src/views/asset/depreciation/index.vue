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
      <el-form-item label="折旧日期" prop="depreciationDate">
        <el-date-picker
            v-model="queryParams.depreciationDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择折旧日期"
            style="width: 180px"
            clearable
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
        <el-button type="primary" plain @click="handleAdd" v-hasPermi="['asset:depreciation:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :disabled="single" @click="handleUpdate"
                   v-hasPermi="['asset:depreciation:edit']">修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                   v-hasPermi="['asset:depreciation:remove']">删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain @click="handleExport" v-hasPermi="['asset:depreciation:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="depreciationList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column align="center" label="折旧ID" prop="id" sortable width="80"/>
      <el-table-column align="center" label="资产编码" prop="assetCode" show-overflow-tooltip sortable
      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.assetCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column label="资产名称" prop="assetName" show-overflow-tooltip sortable
      />
      <el-table-column align="center" label="折旧日期" prop="depreciationDate" sortable>
        <template #default="scope">
          <span>{{ parseTime(scope.row.depreciationDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="right" label="折旧金额" prop="depreciationAmount" sortable>
        <template #default="scope">
          <span>{{ formatMoney(scope.row.depreciationAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="right" label="累计折旧" prop="accumulatedDepreciation" sortable>
        <template #default="scope">
          <span>{{ formatMoney(scope.row.accumulatedDepreciation) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="right" label="净值" prop="netValue" sortable>
        <template #default="scope">
          <span>{{ formatMoney(scope.row.netValue) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['asset:depreciation:query']">详情
          </el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:depreciation:edit']">
            修改
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:depreciation:remove']">
            删除
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

    <!-- 添加或修改资产折旧对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="depreciationRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择资产" prop="assetId">
              <el-select v-model="form.assetId" placeholder="请选择资产" filterable clearable style="width: 100%">
                <el-option
                    v-for="asset in assetOptions"
                    :key="asset.id"
                    :label="`${asset.assetCode} - ${asset.assetName}`"
                    :value="asset.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="折旧日期" prop="depreciationDate">
              <el-date-picker clearable
                              v-model="form.depreciationDate"
                              type="date"
                              value-format="YYYY-MM-DD"
                              placeholder="请选择折旧日期"
                              style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="折旧金额" prop="depreciationAmount">
              <el-input-number v-model="form.depreciationAmount" :precision="2" :min="0" style="width: 100%"
                               placeholder="请输入折旧金额"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="累计折旧" prop="accumulatedDepreciation">
              <el-input-number v-model="form.accumulatedDepreciation" :precision="2" :min="0" style="width: 100%"
                               placeholder="请输入累计折旧"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="净值" prop="netValue">
              <el-input-number v-model="form.netValue" :precision="2" :min="0" style="width: 100%"
                               placeholder="请输入净值"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入内容"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 资产折旧详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="资产折旧详情" size="50%" direction="rtl">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="折旧ID">{{ viewData.id }}</el-descriptions-item>
        <el-descriptions-item label="资产编码">{{ viewData.assetCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ viewData.assetName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="折旧日期">{{
            parseTime(viewData.depreciationDate, '{y}-{m}-{d}') || '-'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="折旧金额">{{ formatMoney(viewData.depreciationAmount) }}</el-descriptions-item>
        <el-descriptions-item label="累计折旧">{{
            formatMoney(viewData.accumulatedDepreciation)
          }}
        </el-descriptions-item>
        <el-descriptions-item label="净值">{{ formatMoney(viewData.netValue) }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ viewData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup lang="ts" name="Depreciation">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import type {Depreciation, DepreciationQueryParams} from "@/types/api/asset/depreciation"
import {
  addDepreciation,
  delDepreciation,
  getDepreciation,
  listDepreciation,
  updateDepreciation
} from "@/api/asset/depreciation"
import {listAssets} from "@/api/asset/assets"
import {parseTime} from '@/utils/ruoyi'
import {formatMoney} from '@/utils/asset'

const {proxy} = getCurrentInstance()

const depreciationList = ref<Depreciation[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const ids = ref<number[]>([])
const single = ref<boolean>(true)
const multiple = ref<boolean>(true)
const total = ref<number>(0)
const title = ref<string>("")
const drawerVisible = ref<boolean>(false)
const viewData = ref<Depreciation>({} as Depreciation)

// 资产选项
const assetOptions = ref([])

const data = reactive({
  form: {} as Depreciation,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    assetName: undefined,
    depreciationDate: undefined,
  } as DepreciationQueryParams,
  rules: {
    assetId: [
      {required: true, message: "请选择资产", trigger: "change"}
    ],
    depreciationDate: [
      {required: true, message: "折旧日期不能为空", trigger: "change"}
    ],
    depreciationAmount: [
      {required: true, message: "折旧金额不能为空", trigger: "blur"}
    ],
  }
})

const {queryParams, form, rules} = toRefs(data)

// 加载资产列表
function loadAssetOptions() {
  listAssets({pageNum: 1, pageSize: 1000}).then(response => {
    assetOptions.value = response.rows || []
  })
}

function getList() {
  loading.value = true
  listDepreciation(queryParams.value).then(response => {
    depreciationList.value = response.rows
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

function handleSelectionChange(selection: Depreciation[]) {
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
    depreciationDate: null,
    depreciationAmount: null,
    accumulatedDepreciation: null,
    netValue: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  }
  proxy.resetForm("depreciationRef")
}

function handleView(row: Depreciation) {
  viewData.value = row
  drawerVisible.value = true
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加资产折旧"
}

function handleUpdate(row: Depreciation) {
  reset()
  const _id = row.id || ids.value[0]
  getDepreciation(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改资产折旧"
  })
}

function submitForm() {
  proxy.$refs["depreciationRef"].validate((valid: boolean) => {
    if (valid) {
      const submitData = form.value.id != null ? updateDepreciation(form.value) : addDepreciation(form.value)
      submitData.then(() => {
        proxy.$modal.msgSuccess(form.value.id != null ? "修改成功" : "新增成功")
        open.value = false
        getList()
      })
    }
  })
}

function handleDelete(row: Depreciation) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除资产折旧编号为"' + _ids + '"的数据项？').then(function () {
    return delDepreciation(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleExport() {
  proxy.download('asset/depreciation/export', {
    ...queryParams.value
  }, `资产折旧_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList()
  loadAssetOptions()
})
</script>
