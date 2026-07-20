<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="计划编号" prop="planCode">
        <el-input v-model="queryParams.planCode" placeholder="请输入计划编号" clearable @keyup.enter="handleQuery"
                  style="width: 180px"/>
      </el-form-item>
      <el-form-item label="计划名称" prop="planName">
        <el-input v-model="queryParams.planName" placeholder="请输入计划名称" clearable @keyup.enter="handleQuery"
                  style="width: 180px"/>
      </el-form-item>
      <el-form-item label="年度" prop="year">
        <el-select v-model="queryParams.year" placeholder="请选择年度" clearable style="width: 180px">
          <el-option v-for="year in yearOptions" :key="year" :label="year + '年'" :value="year"/>
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择" clearable style="width: 180px">
          <el-option v-for="dict in plan_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
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
        <el-button type="primary" plain @click="handleAdd" v-hasPermi="['asset:plan:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :disabled="single" @click="handleUpdate" v-hasPermi="['asset:plan:edit']">修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete" v-hasPermi="['asset:plan:remove']">
          删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :disabled="single" @click="handleSubmit" v-hasPermi="['asset:plan:submit']">
          提交审批
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain @click="handleExport" v-hasPermi="['asset:plan:export']">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="planList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column label="计划编号" prop="planCode" show-overflow-tooltip sortable>
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.planCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column label="计划名称" prop="planName" show-overflow-tooltip sortable/>
      <el-table-column align="center" label="年度" prop="year" sortable width="100">
        <template #default="scope">
          <span>{{ scope.row.year }}年</span>
        </template>
      </el-table-column>
      <el-table-column label="编制部门" prop="deptId" show-overflow-tooltip sortable>
        <template #default="scope">
          <span>{{ getDeptName(scope.row.deptId) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="编制人" prop="userId" show-overflow-tooltip sortable>
        <template #default="scope">
          <span>{{ getUserName(scope.row.userId) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="编制日期" prop="createDate" sortable>
        <template #default="scope">
          <span>{{ parseTime(scope.row.createDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column align="right" label="预算总额" prop="totalBudget" sortable width="140">
        <template #default="scope">
          <span>{{ formatMoney(scope.row.totalBudget) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status" sortable width="100">
        <template #default="scope">
          <dict-tag :options="plan_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="scope">
          <el-button link type="primary" size="small" @click="handleView(scope.row)" v-hasPermi="['asset:plan:query']">
            详情
          </el-button>
          <el-button link type="primary" size="small" @click="handleUpdate(scope.row)"
                     v-if="scope.row.status === 'draft'" v-hasPermi="['asset:plan:edit']">编辑
          </el-button>
          <el-button link type="primary" size="small" @click="handleDelete(scope.row)"
                     v-if="scope.row.status === 'draft'" v-hasPermi="['asset:plan:remove']">删除
          </el-button>
          <el-button link type="primary" size="small" @click="handleSubmit(scope.row)"
                     v-if="scope.row.status === 'draft'" v-hasPermi="['asset:plan:submit']">提交
          </el-button>
          <el-button link type="primary" size="small" @click="handleGeneratePurchase(scope.row)"
                     v-if="scope.row.status === 'approved'" v-hasPermi="['asset:plan:submit']">生成采购单
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum"
                v-model:limit="queryParams.pageSize" @pagination="getList"/>

    <!-- 添加或修改配置计划对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="planRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划编号" prop="planCode">
              <el-input v-model="form.planCode" placeholder="系统自动生成" disabled/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年度" prop="year">
              <el-select v-model="form.year" placeholder="请选择年度" style="width: 100%">
                <el-option v-for="year in yearOptions" :key="year" :label="year + '年'" :value="year"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="计划名称" prop="planName">
              <el-input v-model="form.planName" placeholder="请输入计划名称"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="编制部门" prop="deptId">
              <el-select v-model="form.deptId" placeholder="请选择编制部门" filterable style="width: 100%">
                <el-option v-for="dept in deptOptions" :key="dept.deptId" :label="dept.deptName" :value="dept.deptId"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="编制人" prop="userId">
              <el-select v-model="form.userId" placeholder="请选择编制人" filterable style="width: 100%">
                <el-option v-for="user in userOptions" :key="user.userId" :label="user.nickName" :value="user.userId"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预算总额" prop="totalBudget">
              <el-input-number v-model="form.totalBudget" :min="0" :precision="2" style="width: 100%"
                               placeholder="请输入预算总额"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option v-for="dict in plan_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="1" placeholder="请输入备注" show-word-limit
                        maxlength="500"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">计划明细</el-divider>
        <el-button type="primary" size="small" @click="handleAddDetail" style="margin-bottom: 10px">添加明细</el-button>
        <el-table :data="form.details" border size="small">
          <el-table-column label="资产分类" align="center">
            <template #default="scope">
              <el-select v-model="scope.row.categoryId" placeholder="请选择" style="width: 100%">
                <el-option v-for="cat in categoryOptions" :key="cat.id" :label="cat.categoryName" :value="cat.id"/>
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="资产名称" align="center">
            <template #default="scope">
              <el-input v-model="scope.row.assetName" placeholder="请输入资产名称"/>
            </template>
          </el-table-column>
          <el-table-column label="规格型号" align="center">
            <template #default="scope">
              <el-input v-model="scope.row.model" placeholder="请输入规格型号"/>
            </template>
          </el-table-column>
          <el-table-column label="数量" align="center" width="100">
            <template #default="scope">
              <el-input-number v-model="scope.row.quantity" :min="1" style="width: 100%"/>
            </template>
          </el-table-column>
          <el-table-column label="预估单价" align="center" width="140">
            <template #default="scope">
              <el-input-number v-model="scope.row.estimatedPrice" :min="0" :precision="2" style="width: 100%"/>
            </template>
          </el-table-column>
          <el-table-column label="预估总价" align="center" width="140">
            <template #default="scope">
              <span>{{ formatMoney(scope.row.quantity * scope.row.estimatedPrice) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="80">
            <template #default="scope">
              <el-button link type="danger" size="small" @click="handleDeleteDetail(scope.$index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 配置计划详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="配置计划详情" size="60%" direction="rtl">
      <div class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="计划编号">{{ viewData.planCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="计划名称">{{ viewData.planName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="年度">{{ viewData.year ? viewData.year + '年' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="编制部门">{{ getDeptName(viewData.deptId) }}</el-descriptions-item>
          <el-descriptions-item label="编制人">{{ getUserName(viewData.userId) }}</el-descriptions-item>
          <el-descriptions-item label="编制日期">{{
              parseTime(viewData.createDate, '{y}-{m}-{d}') || '-'
            }}
          </el-descriptions-item>
          <el-descriptions-item label="预算总额">{{ formatMoney(viewData.totalBudget) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <dict-tag :options="plan_status" :value="viewData.status"/>
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider content-position="left">计划明细</el-divider>
        <el-table :data="viewData.details" border size="small">
          <el-table-column label="序号" type="index" width="60" align="center"/>
          <el-table-column label="资产分类" prop="categoryId" align="center">
            <template #default="scope">
              <span>{{ getCategoryName(scope.row.categoryId) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="资产名称" prop="assetName" show-overflow-tooltip/>
          <el-table-column label="规格型号" prop="model" show-overflow-tooltip/>
          <el-table-column label="数量" prop="quantity" width="100" align="right"/>
          <el-table-column label="预估单价" prop="estimatedPrice" width="140" align="right">
            <template #default="scope">
              <span>{{ formatMoney(scope.row.estimatedPrice) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="预估总价" prop="totalPrice" width="140" align="right">
            <template #default="scope">
              <span>{{ formatMoney(scope.row.totalPrice) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-drawer>
  </div>
</template>

<script setup name="AssetPlan">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import {useRouter} from 'vue-router'
import {ElMessage, ElMessageBox} from 'element-plus'
import {addPlan, delPlan, generatePurchase, getPlan, listPlan, submitPlan, updatePlan} from '@/api/asset/plan'
import {listCategory} from '@/api/asset/category'
import {listUser} from '@/api/system/user'
import {listDept} from '@/api/system/dept'

const {proxy} = getCurrentInstance()
const router = useRouter()

const {plan_status} = proxy.useDict('plan_status')

const planList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref('')
const open = ref(false)
const drawerVisible = ref(false)
const viewData = ref({})
const categoryOptions = ref([])
const userOptions = ref([])
const deptOptions = ref([])

// 生成年份选项（当前年份前后5年）
const currentYear = new Date().getFullYear()
const yearOptions = ref([])
for (let i = currentYear - 5; i <= currentYear + 5; i++) {
  yearOptions.value.push(i)
}

const data = reactive({
  form: {
    id: null,
    planCode: null,
    planName: null,
    year: currentYear,
    deptId: null,
    userId: null,
    totalBudget: 0,
    status: 'draft',
    remark: null,
    details: []
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    planCode: null,
    planName: null,
    year: null,
    status: null
  },
  rules: {
    planName: [{required: true, message: '计划名称不能为空', trigger: 'blur'}],
    year: [{required: true, message: '请选择年度', trigger: 'change'}],
    deptId: [{required: true, message: '请选择编制部门', trigger: 'change'}],
    userId: [{required: true, message: '请选择编制人', trigger: 'change'}],
    totalBudget: [{required: true, message: '请输入预算总额', trigger: 'blur'}]
  }
})

const {form, queryParams, rules} = toRefs(data)

/** 查询配置计划列表 */
function getList() {
  loading.value = true
  listPlan(queryParams.value).then(res => {
    planList.value = res.rows || []
    total.value = res.total || 0
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

/** 查询分类列表 */
function getCategoryList() {
  listCategory().then(res => {
    categoryOptions.value = res.data || []
  })
}

/** 查询用户列表 */
function getUserList() {
  listUser({pageNum: 1, pageSize: 1000}).then(res => {
    userOptions.value = res.rows || []
  })
}

/** 查询部门列表 */
function getDeptList() {
  listDept().then(res => {
    deptOptions.value = res.data || []
  })
}

/** 获取分类名称 */
function getCategoryName(categoryId) {
  if (!categoryId) return '-'
  const category = categoryOptions.value.find(cat => cat.id === categoryId)
  return category ? category.categoryName : '-'
}

/** 获取部门名称 */
function getDeptName(deptId) {
  if (!deptId) return '-'
  const dept = deptOptions.value.find(d => d.deptId === deptId)
  return dept ? dept.deptName : '-'
}

/** 获取用户名称 */
function getUserName(userId) {
  if (!userId) return '-'
  const user = userOptions.value.find(u => u.userId === userId)
  return user ? user.nickName : '-'
}

/** 格式化金额 */
function formatMoney(value) {
  if (!value && value !== 0) return '-'
  return value.toLocaleString('zh-CN', {minimumFractionDigits: 2, maximumFractionDigits: 2})
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = '新增资产配置计划'
}

/** 查看详情 */
function handleView(row) {
  getPlan(row.id).then(res => {
    viewData.value = res.data
    drawerVisible.value = true
  })
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  getPlan(id).then(res => {
    form.value = res.data
    open.value = true
    title.value = '修改资产配置计划'
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const deleteIds = row.id || ids.value
  ElMessageBox.confirm('是否确认删除选中的配置计划？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delPlan(deleteIds)
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {
  })
}

/** 提交审批 */
function handleSubmit(row) {
  const id = row.id || ids.value[0]
  ElMessageBox.confirm('确认提交该配置计划进行审批吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return submitPlan(id)
  }).then(() => {
    getList()
    ElMessage.success('提交成功')
  }).catch(() => {
  })
}

/** 生成采购单 */
function handleGeneratePurchase(row) {
  ElMessageBox.confirm('确认根据该配置计划生成采购申请单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return generatePurchase(row.id)
  }).then(() => {
    ElMessage.success('采购单生成成功')
  }).catch(() => {
  })
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('asset/plan/export', {
    ...queryParams.value
  }, `资产配置计划_${new Date().getTime()}.xlsx`)
}

/** 添加明细 */
function handleAddDetail() {
  form.value.details.push({
    categoryId: null,
    assetName: null,
    model: null,
    quantity: 1,
    estimatedPrice: 0
  })
}

/** 删除明细 */
function handleDeleteDetail(index) {
  form.value.details.splice(index, 1)
}

/** 表单重置 */
function reset() {
  form.value = {
    id: null,
    planCode: null,
    planName: null,
    year: currentYear,
    deptId: null,
    userId: null,
    totalBudget: 0,
    status: 'draft',
    remark: null,
    details: []
  }
  proxy.resetForm('planRef')
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 提交表单 */
function submitForm() {
  proxy.$refs.planRef.validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updatePlan(form.value).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addPlan(form.value).then(() => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 解析时间 */
function parseTime(time, pattern) {
  return proxy.parseTime(time, pattern)
}

onMounted(() => {
  getList()
  getCategoryList()
  getUserList()
  getDeptList()
})
</script>

<style scoped lang="scss">
.detail-content {
  padding: 20px;
}
</style>
