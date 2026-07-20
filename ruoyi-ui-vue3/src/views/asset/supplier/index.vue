<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="供应商编码" prop="supplierCode">
        <el-input
            v-model="queryParams.supplierCode"
            placeholder="请输入供应商编码"
            clearable
            @keyup.enter="handleQuery"
            style="width: 150px"
        />
      </el-form-item>
      <el-form-item label="供应商名称" prop="supplierName">
        <el-input
            v-model="queryParams.supplierName"
            placeholder="请输入供应商名称"
            clearable
            @keyup.enter="handleQuery"
            style="width: 150px"
        />
      </el-form-item>
      <el-form-item label="供应商类型" prop="supplierType">
        <el-select v-model="queryParams.supplierType" placeholder="请选择供应商类型" clearable style="width: 150px">
          <el-option v-for="dict in supplier_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="合作状态" prop="cooperationStatus">
        <el-select v-model="queryParams.cooperationStatus" placeholder="请选择合作状态" clearable style="width: 150px">
          <el-option v-for="dict in cooperation_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="信用等级" prop="creditRating">
        <el-select v-model="queryParams.creditRating" placeholder="请选择信用等级" clearable style="width: 150px">
          <el-option v-for="dict in credit_rating" :key="dict.value" :label="dict.label" :value="dict.value"/>
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
        <el-button type="primary" plain @click="handleAdd" v-hasPermi="['asset:supplier:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :disabled="single" @click="handleUpdate" v-hasPermi="['asset:supplier:edit']">
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                   v-hasPermi="['asset:supplier:remove']">删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain @click="handleExport" v-hasPermi="['asset:supplier:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="supplierList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column label="供应商编码" prop="supplierCode" show-overflow-tooltip sortable
      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.supplierCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column label="供应商名称" prop="supplierName" show-overflow-tooltip sortable
      />
      <el-table-column label="简称" prop="shortName" show-overflow-tooltip sortable
      />
      <el-table-column align="center" label="供应商类型" prop="supplierType" sortable>
        <template #default="scope">
          <dict-tag :options="supplier_type" :value="scope.row.supplierType"/>
        </template>
      </el-table-column>
      <el-table-column label="联系人" prop="contactPerson" sortable/>
      <el-table-column label="联系电话" prop="contactPhone" sortable/>
      <el-table-column label="合作状态" prop="cooperationStatus" sortable>
        <template #default="scope">
          <dict-tag :options="cooperation_status" :value="scope.row.cooperationStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="信用等级" prop="creditRating" sortable>
        <template #default="scope">
          <dict-tag :options="credit_rating" :value="scope.row.creditRating"/>
        </template>
      </el-table-column>
      <el-table-column label="地址" prop="address" show-overflow-tooltip sortable
      />
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column align="center" label="操作">
        <template #default="scope">
          <el-button-group>
            <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['asset:supplier:query']">详情
            </el-button>
            <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:supplier:edit']">修改
            </el-button>
            <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:supplier:remove']">
              删除
            </el-button>
          </el-button-group>
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

    <!-- 添加或修改供应商对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="supplierRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商编码" prop="supplierCode">
              <el-input v-model="form.supplierCode" placeholder="请输入供应商编码（如：GY001）"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商名称" prop="supplierName">
              <el-input v-model="form.supplierName" placeholder="请输入供应商名称"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="简称" prop="shortName">
              <el-input v-model="form.shortName" placeholder="请输入简称"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商类型" prop="supplierType">
              <el-select v-model="form.supplierType" placeholder="请选择供应商类型" style="width: 100%">
                <el-option v-for="dict in supplier_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电子邮箱" prop="contactEmail">
              <el-input v-model="form.contactEmail" placeholder="请输入电子邮箱"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合作状态" prop="cooperationStatus">
              <el-select v-model="form.cooperationStatus" placeholder="请选择合作状态" style="width: 100%">
                <el-option v-for="dict in cooperation_status" :key="dict.value" :label="dict.label"
                           :value="dict.value"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="信用等级" prop="creditRating">
              <el-select v-model="form.creditRating" placeholder="请选择信用等级" clearable style="width: 100%">
                <el-option v-for="dict in credit_rating" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="地址" prop="address">
              <el-input v-model="form.address" type="textarea" placeholder="请输入地址" :rows="2"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注信息" :rows="1"/>
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

    <!-- 供应商详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="供应商详情" size="60%" direction="rtl">
      <div class="drawer-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="供应商编码">{{ viewData.supplierCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="供应商名称">{{ viewData.supplierName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="简称">{{ viewData.shortName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="供应商类型">
            <dict-tag :options="supplier_type" :value="viewData.supplierType"/>
          </el-descriptions-item>
          <el-descriptions-item label="联系人">{{ viewData.contactPerson || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ viewData.contactPhone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="电子邮箱">{{ viewData.contactEmail || '-' }}</el-descriptions-item>
          <el-descriptions-item label="合作状态">
            <dict-tag :options="cooperation_status" :value="viewData.cooperationStatus"/>
          </el-descriptions-item>
          <el-descriptions-item label="信用等级">
            <dict-tag :options="credit_rating" :value="viewData.creditRating"/>
          </el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">{{ viewData.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts" name="Supplier">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import type {Supplier, SupplierQueryParams} from "@/types/api/asset/supplier"
import {addSupplier, delSupplier, getSupplier, listSupplier, updateSupplier} from "@/api/asset/supplier"

const {proxy} = getCurrentInstance()
const {
  supplier_type,
  cooperation_status,
  credit_rating
} = proxy.useDict('supplier_type', 'cooperation_status', 'credit_rating')

const supplierList = ref<Supplier[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const ids = ref<number[]>([])
const single = ref<boolean>(true)
const multiple = ref<boolean>(true)
const total = ref<number>(0)
const title = ref<string>("")
const drawerVisible = ref<boolean>(false)
const viewData = ref<Supplier>({} as Supplier)

const data = reactive({
  form: {} as Supplier,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    supplierCode: undefined,
    supplierName: undefined,
    supplierType: undefined,
    cooperationStatus: undefined,
    creditRating: undefined,
  } as SupplierQueryParams,
  rules: {
    supplierCode: [{required: true, message: "供应商编码不能为空", trigger: "blur"}],
    supplierName: [{required: true, message: "供应商名称不能为空", trigger: "blur"}],
    contactPhone: [
      {required: true, message: "联系电话不能为空", trigger: "blur"},
      {pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur"}
    ],
    contactEmail: [{type: "email", message: "请输入正确的邮箱格式", trigger: "blur"}]
  }
})

const {queryParams, form, rules} = toRefs(data)

function getList() {
  loading.value = true
  listSupplier(queryParams.value).then(response => {
    supplierList.value = response.rows
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

function handleSelectionChange(selection: Supplier[]) {
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
    supplierCode: null,
    supplierName: null,
    shortName: null,
    supplierType: null,
    contactPerson: null,
    contactPhone: null,
    contactEmail: null,
    address: null,
    cooperationStatus: 'ACTIVE',
    creditRating: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  }
  proxy.resetForm("supplierRef")
}

function handleView(row: Supplier) {
  viewData.value = row
  drawerVisible.value = true
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加供应商"
}

function handleUpdate(row: Supplier) {
  reset()
  const _id = row.id || ids.value[0]
  getSupplier(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改供应商"
  })
}

function submitForm() {
  proxy.$refs["supplierRef"].validate((valid: boolean) => {
    if (valid) {
      const submitData = form.value.id != null ? updateSupplier(form.value) : addSupplier(form.value)
      submitData.then(() => {
        proxy.$modal.msgSuccess(form.value.id != null ? "修改成功" : "新增成功")
        open.value = false
        getList()
      })
    }
  })
}

function handleDelete(row: Supplier) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除供应商编号为"' + _ids + '"的数据项？').then(function () {
    return delSupplier(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleExport() {
  proxy.download('asset/supplier/export', {...queryParams.value}, `supplier_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList()
})
</script>