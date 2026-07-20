<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="采购单号" prop="purchaseCode">
        <el-input
            v-model="queryParams.purchaseCode"
            placeholder="请输入采购单号"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="采购日期" prop="purchaseDate">
        <el-date-picker
            v-model="queryParams.purchaseDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择采购日期"
            clearable
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="采购状态" prop="purchaseStatus">
        <el-select v-model="queryParams.purchaseStatus" placeholder="请选择采购状态" clearable style="width: 150px">
          <el-option
              v-for="dict in purchase_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">清空</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            @click="handleAdd"
            v-hasPermi="['asset:purchase:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['asset:purchase:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['asset:purchase:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            @click="handleExport"
            v-hasPermi="['asset:purchase:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="purchaseList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column label="采购单号" prop="purchaseCode" show-overflow-tooltip sortable
      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.purchaseCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column align="center" label="采购日期" prop="purchaseDate" sortable>
        <template #default="scope">
          <span>{{ parseTime(scope.row.purchaseDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="供应商" prop="supplierName" show-overflow-tooltip sortable
      />
      <el-table-column align="right" label="采购金额" min- prop="purchaseAmount" sortable>
        <template #default="scope">
          <span>{{ formatMoney(scope.row.purchaseAmount) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="采购状态" prop="purchaseStatus" sortable width="100">
        <template #default="scope">
          <dict-tag :options="purchase_status" :value="scope.row.purchaseStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['asset:purchase:query']">详情
          </el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:purchase:edit']">修改
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:purchase:remove']">删除
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

    <!-- 添加或修改资产采购对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="purchaseRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购单号" prop="purchaseCode">
              <el-input v-model="form.purchaseCode" placeholder="请输入采购单号"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购日期" prop="purchaseDate">
              <el-date-picker clearable
                              v-model="form.purchaseDate"
                              type="date"
                              value-format="YYYY-MM-DD"
                              placeholder="请选择采购日期"
                              style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="form.supplierId" placeholder="请选择供应商" filterable clearable style="width: 100%">
                <el-option
                    v-for="sup in supplierOptions"
                    :key="sup.id"
                    :label="sup.supplierName"
                    :value="sup.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购金额" prop="purchaseAmount">
              <el-input-number v-model="form.purchaseAmount" :precision="2" :min="0" style="width: 100%"
                               placeholder="请输入采购金额"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购状态" prop="purchaseStatus">
              <el-select v-model="form.purchaseStatus" placeholder="请选择采购状态" style="width: 100%">
                <el-option
                    v-for="dict in purchase_status"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                />
              </el-select>
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
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </template>
    </el-dialog>

    <!-- 资产采购详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="资产采购详情" size="50%" direction="rtl">
      <div class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="采购单号">{{ viewData.purchaseCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="采购日期">{{
              parseTime(viewData.purchaseDate, '{y}-{m}-{d}') || '-'
            }}
          </el-descriptions-item>
          <el-descriptions-item label="供应商">{{ getSupplierName(viewData.supplierId) }}</el-descriptions-item>
          <el-descriptions-item label="采购金额">{{ formatMoney(viewData.purchaseAmount) }}</el-descriptions-item>
          <el-descriptions-item label="采购状态">
            <dict-tag :options="purchase_status" :value="viewData.purchaseStatus"/>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建人">{{ viewData.createBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ parseTime(viewData.createTime) || '-' }}</el-descriptions-item>
          <el-descriptions-item label="更新人">{{ viewData.updateBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ parseTime(viewData.updateTime) || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script setup name="Purchase">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import {addPurchase, delPurchase, getPurchase, listPurchase, updatePurchase} from "@/api/asset/purchase"
import {formatMoney} from '@/utils/asset'
import {useSupplierOptions} from '@/composables/asset/useSupplierOptions'

const {proxy} = getCurrentInstance()
const {purchase_status} = proxy.useDict('purchase_status')

const purchaseList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const drawerVisible = ref(false)
const viewData = ref({})
// 使用 Composable 加载供应商选项和名称映射
const {supplierOptions, loadSupplierOptions, getSupplierName} = useSupplierOptions()

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    purchaseCode: undefined,
    purchaseDate: undefined,
    purchaseStatus: undefined,
  },
  rules: {
    purchaseCode: [
      {required: true, message: "采购单号不能为空", trigger: "blur"}
    ],
    purchaseDate: [
      {required: true, message: "采购日期不能为空", trigger: "change"}
    ],
    supplierId: [
      {required: true, message: "供应商不能为空", trigger: "change"}
    ],
    purchaseAmount: [
      {required: true, message: "采购金额不能为空", trigger: "blur"}
    ],
  }
})

const {queryParams, form, rules} = toRefs(data)

/** 查询资产采购列表 */
function getList() {
  loading.value = true
  listPurchase(queryParams.value).then(response => {
    purchaseList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    purchaseCode: null,
    purchaseDate: null,
    supplierId: null,
    purchaseAmount: null,
    purchaseStatus: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  }
  proxy.resetForm("purchaseRef")
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleView(row) {
  viewData.value = row
  drawerVisible.value = true
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加资产采购"
}

function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value[0]
  getPurchase(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改资产采购"
  })
}

function submitForm() {
  proxy.$refs["purchaseRef"].validate((valid) => {
    if (valid) {
      const submitData = form.value.id != null ? updatePurchase(form.value) : addPurchase(form.value)
      submitData.then(() => {
        proxy.$modal.msgSuccess(form.value.id != null ? "修改成功" : "新增成功")
        open.value = false
        getList()
      })
    }
  })
}

function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除资产采购编号为"' + _ids + '"的数据项？').then(function () {
    return delPurchase(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleExport() {
  proxy.download('asset/purchase/export', {
    ...queryParams.value
  }, `purchase_${new Date().getTime()}.xlsx`)
}

// 初始化加载
onMounted(async () => {
  // 加载供应商选项
  await loadSupplierOptions()

  // 加载列表
  getList()
})
</script>