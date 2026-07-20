<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="资产名称" prop="assetName">
        <el-input
            v-model="queryParams.assetName"
            placeholder="请输入资产名称"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="转固类型" prop="capitalizeType">
        <el-select v-model="queryParams.capitalizeType" placeholder="请选择转固类型" clearable style="width: 150px">
          <el-option
              v-for="dict in capitalize_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="转固日期" prop="capitalizeDate">
        <el-date-picker
            v-model="queryParams.capitalizeDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择转固日期"
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
        <el-button type="primary" plain @click="handleAdd" v-hasPermi="['asset:capitalize:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :disabled="single" @click="handleUpdate" v-hasPermi="['asset:capitalize:edit']">
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                   v-hasPermi="['asset:capitalize:remove']">删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain @click="handleExport" v-hasPermi="['asset:capitalize:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="capitalizeList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column align="center" label="转固ID" prop="id" sortable width="80"/>
      <el-table-column label="资产编码" prop="assetCode" show-overflow-tooltip sortable
      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.assetCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column label="资产名称" prop="assetName" show-overflow-tooltip sortable
      />
      <el-table-column align="center" label="转固类型" prop="capitalizeType" sortable>
        <template #default="scope">
          <dict-tag :options="capitalize_type" :value="scope.row.capitalizeType"/>
        </template>
      </el-table-column>
      <el-table-column align="right" label="转固金额" prop="capitalizeAmount" sortable>
        <template #default="scope">
          <span>{{ formatMoney(scope.row.capitalizeAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="转固日期" prop="capitalizeDate" sortable>
        <template #default="scope">
          <span>{{ parseTime(scope.row.capitalizeDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['asset:capitalize:query']">详情
          </el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:capitalize:edit']">修改
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:capitalize:remove']">
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

    <!-- 添加或修改资产转固对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="capitalizeRef" :model="form" :rules="rules" label-width="120px">
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
            <el-form-item label="转固类型" prop="capitalizeType">
              <el-select v-model="form.capitalizeType" placeholder="请选择转固类型" style="width: 100%">
                <el-option
                    v-for="dict in capitalize_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="转固金额" prop="capitalizeAmount">
              <el-input-number v-model="form.capitalizeAmount" :precision="2" :min="0" style="width: 100%"
                               placeholder="请输入转固金额"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="转固日期" prop="capitalizeDate">
              <el-date-picker clearable
                              v-model="form.capitalizeDate"
                              type="date"
                              value-format="YYYY-MM-DD"
                              placeholder="请选择转固日期"
                              style="width: 100%">
              </el-date-picker>
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

    <!-- 资产转固详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="资产转固详情" size="50%" direction="rtl">
      <div class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="转固ID">{{ viewData.id }}</el-descriptions-item>
          <el-descriptions-item label="资产编码">{{ viewData.assetCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="资产名称">{{ viewData.assetName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="转固类型">
            <dict-tag :options="capitalize_type" :value="viewData.capitalizeType"/>
          </el-descriptions-item>
          <el-descriptions-item label="转固金额">{{ formatMoney(viewData.capitalizeAmount) }}</el-descriptions-item>
          <el-descriptions-item label="转固日期">{{
              parseTime(viewData.capitalizeDate, '{y}-{m}-{d}') || '-'
            }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts" name="Capitalize">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import type {Capitalize, CapitalizeQueryParams} from "@/types/api/asset/capitalize"
import {addCapitalize, delCapitalize, getCapitalize, listCapitalize, updateCapitalize} from "@/api/asset/capitalize"
import {listAssets} from "@/api/asset/assets"
import {parseTime} from '@/utils/ruoyi'
import {formatMoney} from '@/utils/asset'

const {proxy} = getCurrentInstance()
const {capitalize_type} = proxy.useDict('capitalize_type')

const capitalizeList = ref<Capitalize[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const ids = ref<number[]>([])
const single = ref<boolean>(true)
const multiple = ref<boolean>(true)
const total = ref<number>(0)
const title = ref<string>("")
const drawerVisible = ref<boolean>(false)
const viewData = ref<Capitalize>({} as Capitalize)

// 资产选项
const assetOptions = ref([])

const data = reactive({
  form: {} as Capitalize,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    assetName: undefined,
    capitalizeType: undefined,
    capitalizeDate: undefined,
  } as CapitalizeQueryParams,
  rules: {
    assetId: [
      {required: true, message: "请选择资产", trigger: "change"}
    ],
    capitalizeType: [
      {required: true, message: "转固类型不能为空", trigger: "change"}
    ],
    capitalizeAmount: [
      {required: true, message: "转固金额不能为空", trigger: "blur"}
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
  listCapitalize(queryParams.value).then(response => {
    capitalizeList.value = response.rows
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

function handleSelectionChange(selection: Capitalize[]) {
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
    capitalizeType: null,
    capitalizeAmount: null,
    capitalizeDate: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  }
  proxy.resetForm("capitalizeRef")
}

function handleView(row: Capitalize) {
  viewData.value = row
  drawerVisible.value = true
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加资产转固"
}

function handleUpdate(row: Capitalize) {
  reset()
  const _id = row.id || ids.value[0]
  getCapitalize(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改资产转固"
  })
}

function submitForm() {
  proxy.$refs["capitalizeRef"].validate((valid: boolean) => {
    if (valid) {
      const submitData = form.value.id != null ? updateCapitalize(form.value) : addCapitalize(form.value)
      submitData.then(() => {
        proxy.$modal.msgSuccess(form.value.id != null ? "修改成功" : "新增成功")
        open.value = false
        getList()
      })
    }
  })
}

function handleDelete(row: Capitalize) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除资产转固编号为"' + _ids + '"的数据项？').then(function () {
    return delCapitalize(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleExport() {
  proxy.download('asset/capitalize/export', {
    ...queryParams.value
  }, `转固记录_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList()
  loadAssetOptions()
})
</script>
