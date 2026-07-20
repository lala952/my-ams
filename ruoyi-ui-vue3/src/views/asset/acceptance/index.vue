<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="验收编码" prop="acceptanceCode">
        <el-input
            v-model="queryParams.acceptanceCode"
            placeholder="请输入验收编码"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="申购单号" prop="purchaseCode">
        <el-input
            v-model="queryParams.purchaseCode"
            placeholder="请输入申购单号"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="供应商" prop="supplierId">
        <el-select v-model="queryParams.supplierId" placeholder="请选择供应商" clearable filterable
                   style="width: 180px">
          <el-option
              v-for="sup in supplierOptions"
              :key="sup.id"
              :label="sup.supplierName"
              :value="sup.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="验收结果" prop="acceptanceResult">
        <el-select v-model="queryParams.acceptanceResult" placeholder="请选择验收结果" clearable style="width: 150px">
          <el-option
              v-for="dict in acceptance_result"
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
        <el-button type="primary" plain @click="handleAdd" v-hasPermi="['asset:acceptance:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :disabled="single" @click="handleUpdate" v-hasPermi="['asset:acceptance:edit']">
          修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                   v-hasPermi="['asset:acceptance:remove']">删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain @click="handleExport" v-hasPermi="['asset:acceptance:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="acceptanceList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column label="验收编码" prop="acceptanceCode" show-overflow-tooltip sortable
      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.acceptanceCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column label="申购单号" prop="purchaseCode" show-overflow-tooltip sortable
      />
      <el-table-column label="供应商" prop="supplierName" show-overflow-tooltip sortable
      />
      <el-table-column label="发票号码" prop="invoiceNo" show-overflow-tooltip sortable
      />
      <el-table-column label="合同编号" prop="contractNo" show-overflow-tooltip sortable
      />
      <el-table-column align="center" label="验收日期" prop="acceptanceDate" sortable>
        <template #default="scope">
          <span>{{ parseTime(scope.row.acceptanceDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="验收结果" prop="acceptanceResult" sortable width="100">
        <template #default="scope">
          <dict-tag :options="acceptance_result" :value="scope.row.acceptanceResult"/>
        </template>
      </el-table-column>
      <el-table-column align="right" label="合格数量" prop="qualifiedQuantity" sortable width="100"/>
      <el-table-column label="验收人" min-width="100" prop="acceptancePersonName" show-overflow-tooltip sortable
      />
      <el-table-column align="center" label="状态" prop="status" sortable width="100">
        <template #default="scope">
          <dict-tag :options="bill_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['asset:acceptance:query']">详情
          </el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:acceptance:edit']">修改
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:acceptance:remove']">
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

    <!-- 添加或修改资产验收对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="acceptanceRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="验收编码" prop="acceptanceCode">
              <el-input v-model="form.acceptanceCode" placeholder="请输入验收编码"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申购单号" prop="purchaseId">
              <el-select v-model="form.purchaseId" placeholder="请选择申购单" filterable clearable style="width: 100%">
                <el-option
                    v-for="purchase in purchaseOptions"
                    :key="purchase.id"
                    :label="purchase.purchaseCode"
                    :value="purchase.id"
                />
              </el-select>
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
            <el-form-item label="发票号码" prop="invoiceNo">
              <el-input v-model="form.invoiceNo" placeholder="请输入发票号码"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同编号" prop="contractNo">
              <el-input v-model="form.contractNo" placeholder="请输入合同编号"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="验收日期" prop="acceptanceDate">
              <el-date-picker clearable
                              v-model="form.acceptanceDate"
                              type="date"
                              value-format="YYYY-MM-DD"
                              placeholder="请选择验收日期"
                              style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="验收结果" prop="acceptanceResult">
              <el-select v-model="form.acceptanceResult" placeholder="请选择验收结果" style="width: 100%">
                <el-option
                    v-for="dict in acceptance_result"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合格数量" prop="qualifiedQuantity">
              <el-input-number v-model="form.qualifiedQuantity" :min="0" style="width: 100%"
                               placeholder="请输入合格数量"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="验收人" prop="acceptancePersonId">
              <el-select v-model="form.acceptancePersonId" placeholder="请选择验收人" filterable clearable
                         style="width: 100%">
                <el-option
                    v-for="user in userOptions"
                    :key="user.userId"
                    :label="user.nickName"
                    :value="user.userId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option
                    v-for="dict in bill_status"
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
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 资产验收详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="资产验收详情" size="50%" direction="rtl">
      <div class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="验收编码">{{ viewData.acceptanceCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申购单号">{{ viewData.purchaseCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="供应商">{{ viewData.supplierName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发票号码">{{ viewData.invoiceNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="合同编号">{{ viewData.contractNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="验收日期">{{
              parseTime(viewData.acceptanceDate, '{y}-{m}-{d}') || '-'
            }}
          </el-descriptions-item>
          <el-descriptions-item label="验收结果">
            <dict-tag :options="acceptance_result" :value="viewData.acceptanceResult"/>
          </el-descriptions-item>
          <el-descriptions-item label="合格数量">{{ viewData.qualifiedQuantity || '-' }}</el-descriptions-item>
          <el-descriptions-item label="验收人">{{ viewData.acceptancePersonName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <dict-tag :options="bill_status" :value="viewData.status"/>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>
  </div>
</template>

<script setup name="Acceptance">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import {addAcceptance, delAcceptance, getAcceptance, listAcceptance, updateAcceptance} from "@/api/asset/acceptance"
import {listPurchase} from '@/api/asset/purchase'
import {parseTime} from '@/utils/ruoyi'

import {useUserOptions} from '@/composables/asset/useUserOptions'
import {useSupplierOptions} from '@/composables/asset/useSupplierOptions'

const {proxy} = getCurrentInstance()

const {acceptance_result, bill_status} = proxy.useDict('acceptance_result', 'bill_status')

const acceptanceList = ref([])
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

// 使用 Composable 加载选项数据
const {userOptions, loadUserOptions} = useUserOptions()
const {supplierOptions, loadSupplierOptions} = useSupplierOptions()
const purchaseOptions = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    acceptanceCode: undefined,
    purchaseCode: undefined,
    supplierId: undefined,
    acceptanceResult: undefined,
  },
  rules: {
    acceptanceCode: [
      {required: true, message: "验收编码不能为空", trigger: "blur"}
    ],
    acceptanceDate: [
      {required: true, message: "验收日期不能为空", trigger: "change"}
    ],
  }
})

const {queryParams, form, rules} = toRefs(data)

// 加载申购单列表
function loadPurchaseOptions() {
  listPurchase({pageNum: 1, pageSize: 100}).then(res => {
    purchaseOptions.value = res.rows || []
  })
}

function getList() {
  loading.value = true
  listAcceptance(queryParams.value).then(response => {
    // 批量处理空值
    acceptanceList.value = (response.rows || []).map(item => {
      const processed = {}
      // 处理所有字段
      Object.keys(item).forEach(key => {
        processed[key] = item[key] || "-"
      })
      return processed
    })
    total.value = response.total
    loading.value = false
  }).catch(() => {
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

function handleSelectionChange(selection) {
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
    acceptanceCode: null,
    purchaseId: null,
    supplierId: null,
    invoiceNo: null,
    contractNo: null,
    acceptanceDate: null,
    acceptanceResult: null,
    qualifiedQuantity: null,
    acceptancePersonId: null,
    status: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  }
  proxy.resetForm("acceptanceRef")
}

function handleView(row) {
  viewData.value = row
  drawerVisible.value = true
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "添加资产验收"
}

function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value[0]
  getAcceptance(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改资产验收"
  })
}

function submitForm() {
  proxy.$refs["acceptanceRef"].validate((valid) => {
    if (valid) {
      const submitData = form.value.id != null ? updateAcceptance(form.value) : addAcceptance(form.value)
      submitData.then(() => {
        proxy.$modal.msgSuccess(form.value.id != null ? "修改成功" : "新增成功")
        open.value = false
        getList()
      }).catch((error) => {
        proxy.$modal.msgError(error.message || "操作失败")
      })
    }
  })
}

function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除资产验收编号为"' + _ids + '"的数据项？').then(function () {
    return delAcceptance(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleExport() {
  proxy.download('asset/acceptance/export', {
    ...queryParams.value
  }, `资产验收_${new Date().getTime()}.xlsx`)
}

onMounted(async () => {
  // 加载选项数据
  await Promise.all([
    loadUserOptions(),
    loadSupplierOptions()
  ])

  // 加载列表和申购单选项
  getList()
  loadPurchaseOptions()
})
</script>