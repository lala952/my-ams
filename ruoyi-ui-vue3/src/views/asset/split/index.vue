<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="拆分单编码" prop="splitCode">
        <el-input
            v-model="queryParams.splitCode"
            placeholder="请输入拆分单编码"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="拆分日期" prop="splitDate">
        <el-date-picker clearable
                        v-model="queryParams.splitDate"
                        type="date"
                        value-format="YYYY-MM-DD"
                        placeholder="请选择拆分日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="业务状态" prop="businessStatus">
        <el-select v-model="queryParams.businessStatus" placeholder="请选择业务状态" clearable>
          <el-option
              v-for="dict in business_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="审批时间" prop="approveTime">
        <el-date-picker clearable
                        v-model="queryParams.approveTime"
                        type="date"
                        value-format="YYYY-MM-DD"
                        placeholder="请选择审批时间">
        </el-date-picker>
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
            v-hasPermi="['asset:split:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['asset:split:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['asset:split:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            @click="handleExport"
            v-hasPermi="['asset:split:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="splitList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center"/>
      <el-table-column label="拆分ID" align="center" prop="id"/>
      <el-table-column label="拆分单编码" align="center" prop="splitCode"/>
      <el-table-column label="原资产ID" align="center" prop="sourceAssetId"/>
      <el-table-column label="拆分原因" align="center" prop="splitReason"/>
      <el-table-column label="拆分日期" align="center" prop="splitDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.splitDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="拆分总数" align="center" prop="splitQuantity"/>
      <el-table-column label="业务状态" align="center" prop="businessStatus">
        <template #default="scope">
          <dict-tag :options="business_status" :value="scope.row.businessStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="审批人ID" align="center" prop="approverId"/>
      <el-table-column label="审批时间" align="center" prop="approveTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.approveTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" @click="handleViewData(scope.row)"
                     v-hasPermi="['asset:split:query']">详情
          </el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:split:edit']">
            修改
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:split:remove']">
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

    <split-view-drawer ref="splitViewRef" />
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="splitRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="拆分单编码" prop="splitCode">
              <el-input v-model="form.splitCode" placeholder="请输入拆分单编码"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="拆分原因" prop="splitReason">
              <el-input v-model="form.splitReason" type="textarea" placeholder="请输入内容"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="拆分日期" prop="splitDate">
              <el-date-picker clearable
                              v-model="form.splitDate"
                              type="date"
                              value-format="YYYY-MM-DD"
                              placeholder="请选择拆分日期">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="拆分总数" prop="splitQuantity">
              <el-input v-model="form.splitQuantity" placeholder="请输入拆分总数"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务状态" prop="businessStatus">
              <el-select v-model="form.businessStatus" placeholder="请选择业务状态">
                <el-option
                    v-for="dict in business_status"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审批时间" prop="approveTime">
              <el-date-picker clearable
                              v-model="form.approveTime"
                              type="date"
                              value-format="YYYY-MM-DD"
                              placeholder="请选择审批时间">
              </el-date-picker>
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

<script setup name="Split">
import { listSplit, getSplit, addSplit, updateSplit, delSplit } from "@/api/asset/split"
import SplitViewDrawer from './view.vue'

const { proxy } = getCurrentInstance()
const { business_status } = useDict('business_status')

const splitList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const splitViewRef = ref(null)
const queryRef = ref(null)
const splitRef = ref(null)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    splitCode: undefined,
    sourceAssetId: undefined,
    splitDate: undefined,
    businessStatus: undefined,
    approverId: undefined,
    approveTime: undefined,
  },
  rules: {
    sourceAssetId: [{ required: true, message: "原资产ID不能为空", trigger: "change" }],
    splitReason: [{ required: true, message: "拆分原因不能为空", trigger: "blur" }],
    businessStatus: [{ required: true, message: "业务状态不能为空", trigger: "change" }],
  }
})

const { queryParams, form, rules } = toRefs(data)

function getList() {
  loading.value = true
  listSplit(queryParams.value).then(response => {
    splitList.value = response.rows
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
    splitCode: null,
    sourceAssetId: null,
    splitReason: null,
    splitDate: null,
    splitQuantity: null,
    businessStatus: null,
    approverId: null,
    approveTime: null,
    procInstId: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  }
  if (splitRef.value) splitRef.value.resetFields()
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
  title.value = "添加资产拆分明细"
}

function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value[0]
  getSplit(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改资产拆分明细"
  })
}

function submitForm() {
  if (!splitRef.value) return
  splitRef.value.validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateSplit(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addSplit(form.value).then(() => {
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
  proxy.$modal.confirm('是否确认删除资产拆分明细编号为"' + _ids + '"的数据项？').then(function () {
    return delSplit(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleViewData(row) {
  if (splitViewRef.value) splitViewRef.value.open(row.id)
}

function handleExport() {
  proxy.download('asset/split/export', {
    ...queryParams.value
  }, `split_${new Date().getTime()}.xlsx`)
}

getList()
</script>