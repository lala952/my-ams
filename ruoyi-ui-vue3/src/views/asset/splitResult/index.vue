<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="新资产编码" prop="newAssetCode">
        <el-input
            v-model="queryParams.newAssetCode"
            placeholder="请输入新资产编码"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="新资产名称" prop="newAssetName">
        <el-input
            v-model="queryParams.newAssetName"
            placeholder="请输入新资产名称"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="拆分数量" prop="splitQuantity">
        <el-input
            v-model="queryParams.splitQuantity"
            placeholder="请输入拆分数量"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分摊原值" prop="originalValue">
        <el-input
            v-model="queryParams.originalValue"
            placeholder="请输入分摊原值"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="存放位置" prop="location">
        <el-input
            v-model="queryParams.location"
            placeholder="请输入存放位置"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="执行状态" prop="executeStatus">
        <el-select v-model="queryParams.executeStatus" placeholder="请选择执行状态" clearable>
          <el-option
              v-for="dict in execute_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="生成的资产ID" prop="targetAssetId">
        <el-input
            v-model="queryParams.targetAssetId"
            placeholder="请输入生成的资产ID"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">清空</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            @click="handleAdd"
            v-hasPermi="['asset:splitResult:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['asset:splitResult:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['asset:splitResult:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            @click="handleExport"
            v-hasPermi="['asset:splitResult:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="splitResultList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center"/>
      <el-table-column label="记录ID" align="center" prop="id"/>
      <el-table-column label="拆分单ID" align="center" prop="splitId"/>
      <el-table-column label="新资产编码" align="center" prop="newAssetCode"/>
      <el-table-column label="新资产名称" align="center" prop="newAssetName"/>
      <el-table-column label="拆分数量" align="center" prop="splitQuantity"/>
      <el-table-column label="分摊原值" align="center" prop="originalValue"/>
      <el-table-column label="使用人ID" align="center" prop="userId"/>
      <el-table-column label="使用部门ID" align="center" prop="deptId"/>
      <el-table-column label="存放位置" align="center" prop="location"/>
      <el-table-column label="执行状态" align="center" prop="executeStatus">
        <template #default="scope">
          <dict-tag :options="execute_status" :value="scope.row.executeStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="生成的资产ID" align="center" prop="targetAssetId"/>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" @click="handleViewData(scope.row)"
                     v-hasPermi="['asset:splitResult:query']">详情
          </el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:splitResult:edit']">
            修改
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:splitResult:remove']">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <splitResult-view-drawer ref="splitResultViewRef" />
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="splitResultRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="新资产编码" prop="newAssetCode">
              <el-input v-model="form.newAssetCode" placeholder="请输入新资产编码"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="新资产名称" prop="newAssetName">
              <el-input v-model="form.newAssetName" placeholder="请输入新资产名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="拆分数量" prop="splitQuantity">
              <el-input v-model="form.splitQuantity" placeholder="请输入拆分数量"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分摊原值" prop="originalValue">
              <el-input v-model="form.originalValue" placeholder="请输入分摊原值"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="存放位置" prop="location">
              <el-input v-model="form.location" placeholder="请输入存放位置"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行状态" prop="executeStatus">
              <el-select v-model="form.executeStatus" placeholder="请选择执行状态">
                <el-option
                    v-for="dict in execute_status"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生成的资产ID" prop="targetAssetId">
              <el-input v-model="form.targetAssetId" placeholder="请输入生成的资产ID"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"/>
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
  </div>
</template>

<script setup name="SplitResult">
import { listSplitResult, getSplitResult, addSplitResult, updateSplitResult, delSplitResult } from "@/api/asset/splitResult"
import SplitResultViewDrawer from './view.vue'

const { proxy } = getCurrentInstance()
const { execute_status } = useDict('execute_status')

const splitResultList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const splitResultViewRef = ref(null)
const queryRef = ref(null)
const splitResultRef = ref(null)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    splitId: undefined,
    newAssetCode: undefined,
    newAssetName: undefined,
    splitQuantity: undefined,
    originalValue: undefined,
    userId: undefined,
    deptId: undefined,
    location: undefined,
    executeStatus: undefined,
    targetAssetId: undefined,
  },
  rules: {
    splitId: [{ required: true, message: "拆分单ID不能为空", trigger: "change" }],
    newAssetCode: [{ required: true, message: "新资产编码不能为空", trigger: "blur" }],
    newAssetName: [{ required: true, message: "新资产名称不能为空", trigger: "blur" }],
    splitQuantity: [{ required: true, message: "拆分数量不能为空", trigger: "blur" }],
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listSplitResult(queryParams.value).then(response => {
    splitResultList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: null,
    splitId: null,
    newAssetCode: null,
    newAssetName: null,
    splitQuantity: null,
    originalValue: null,
    userId: null,
    deptId: null,
    location: null,
    executeStatus: null,
    targetAssetId: null,
    remark: null,
    createTime: null,
    createBy: null,
    updateTime: null,
    updateBy: null,
    delFlag: null
  }
  if (splitResultRef.value) splitResultRef.value.resetFields()
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  if (queryRef.value) queryRef.value.resetFields()
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加资产拆分结果"
}

function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value[0]
  getSplitResult(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改资产拆分结果"
  })
}

function submitForm() {
  if (!splitResultRef.value) return
  splitResultRef.value.validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateSplitResult(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addSplitResult(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const _ids = row.id || ids.value.join(',')
  proxy.$modal.confirm('是否确认删除资产拆分结果编号为"' + _ids + '"的数据项？').then(function () {
    return delSplitResult(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleViewData(row) {
  if (splitResultViewRef.value) splitResultViewRef.value.open(row.id)
}

function handleExport() {
  proxy.download('asset/splitResult/export', {
    ...queryParams.value
  }, `splitResult_${new Date().getTime()}.xlsx`)
}

getList()
</script>