<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="领用单号" prop="receiveCode">
        <el-input
            v-model="queryParams.receiveCode"
            placeholder="请输入领用单号"
            clearable
            @keyup.enter="handleQuery"
            style="width: 160px"
        />
      </el-form-item>
      <el-form-item label="资产名称" prop="assetName">
        <el-input
            v-model="queryParams.assetName"
            placeholder="请输入资产名称"
            clearable
            @keyup.enter="handleQuery"
            style="width: 160px"
        />
      </el-form-item>
      <el-form-item label="领用日期" prop="receiveDate">
        <el-date-picker
            v-model="queryParams.receiveDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择领用日期"
            style="width: 160px"
            clearable
        />
      </el-form-item>
      <el-form-item label="领用人" prop="receivePersonId">
        <el-select v-model="queryParams.receivePersonId" placeholder="请选择领用人" clearable filterable
                   style="width: 160px">
          <el-option
              v-for="user in userOptions"
              :key="user.userId"
              :label="user.nickName"
              :value="user.userId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="领用部门" prop="deptId">
        <el-tree-select
            v-model="queryParams.deptId"
            :data="deptOptions"
            :props="{ value: 'deptId', label: 'deptName' }"
            placeholder="请选择领用部门"
            check-strictly
            style="width: 160px"
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
        <el-button
            type="primary"
            plain
            @click="handleAdd"
            v-hasPermi="['asset:receive:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['asset:receive:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['asset:receive:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            @click="handleExport"
            v-hasPermi="['asset:receive:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="receiveList" @selection-change="handleSelectionChange" border>
      <el-table-column type="selection" width="50" align="center" fixed="left"/>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>
      <el-table-column label="领用单号" prop="receiveCode" show-overflow-tooltip sortable
      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.receiveCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column label="资产名称" prop="assetName" show-overflow-tooltip sortable
      />
      <el-table-column label="资产编码" prop="assetCode" show-overflow-tooltip sortable
      />
      <el-table-column align="center" label="领用日期" prop="receiveDate" sortable>
        <template #default="scope">
          <span>{{ parseTime(scope.row.receiveDate, '{y}-{m}-{d}') || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="领用人" min-width="100" prop="receivePersonName" show-overflow-tooltip sortable
      />
      <el-table-column label="领用部门" min- prop="deptName" show-overflow-tooltip sortable
      />
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column align="center" fixed="right" label="操作" width="180">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)" v-hasPermi="['asset:receive:query']">详情
          </el-button>
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:receive:edit']">修改
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:receive:remove']">删除
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

    <!-- 添加或修改资产领用对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="receiveRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="领用单号" prop="receiveCode">
              <el-input v-model="form.receiveCode" placeholder="请输入领用单号"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="领用日期" prop="receiveDate">
              <el-date-picker clearable
                              v-model="form.receiveDate"
                              type="date"
                              value-format="YYYY-MM-DD"
                              placeholder="请选择领用日期"
                              style="width: 100%">
              </el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
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
            <el-form-item label="领用人" prop="receivePersonId">
              <el-select v-model="form.receivePersonId" placeholder="请选择领用人" filterable clearable
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
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="领用部门" prop="deptId">
              <el-tree-select
                  v-model="form.deptId"
                  :data="deptOptions"
                  :props="{ value: 'deptId', label: 'deptName' }"
                  placeholder="请选择领用部门"
                  check-strictly
                  style="width: 100%"
              />
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

    <!-- 资产领用详情抽屉 -->
    <el-drawer v-model="drawerVisible" title="资产领用详情" size="50%" direction="rtl">
      <div class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="领用单号">{{ viewData.receiveCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="领用日期">{{
              parseTime(viewData.receiveDate, '{y}-{m}-{d}') || '-'
            }}
          </el-descriptions-item>
          <el-descriptions-item label="资产名称">{{ viewData.assetName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="资产编码">{{ viewData.assetCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="领用人">{{ viewData.receivePersonName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="领用部门">{{ viewData.deptName || '-' }}</el-descriptions-item>
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

<script setup name="Receive">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import {addReceive, delReceive, getReceive, listReceive, updateReceive} from "@/api/asset/receive"
import {listAssets} from "@/api/asset/assets"
import {parseTime} from '@/utils/ruoyi'
import {useUserOptions} from '@/composables/asset/useUserOptions'
import {useDeptOptions} from '@/composables/asset/useDeptOptions'

const {proxy} = getCurrentInstance()

const receiveList = ref([])
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
const {deptOptions, loadDeptOptions} = useDeptOptions()
const assetOptions = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    receiveCode: undefined,
    assetName: undefined,
    receiveDate: undefined,
    receivePersonId: undefined,
    deptId: undefined,
  },
  rules: {
    receiveCode: [
      {required: true, message: "领用单号不能为空", trigger: "blur"}
    ],
    assetId: [
      {required: true, message: "请选择资产", trigger: "change"}
    ],
    receiveDate: [
      {required: true, message: "领用日期不能为空", trigger: "change"}
    ],
    receivePersonId: [
      {required: true, message: "请选择领用人", trigger: "change"}
    ],
    deptId: [
      {required: true, message: "请选择领用部门", trigger: "change"}
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

/** 查询资产领用列表 */
function getList() {
  loading.value = true
  listReceive(queryParams.value).then(response => {
    receiveList.value = response.rows
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
    receiveCode: null,
    assetId: null,
    receiveDate: null,
    receivePersonId: null,
    deptId: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  }
  proxy.resetForm("receiveRef")
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
  title.value = "添加资产领用"
}

function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value[0]
  getReceive(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改资产领用"
  })
}

function submitForm() {
  proxy.$refs["receiveRef"].validate((valid) => {
    if (valid) {
      const submitData = form.value.id != null ? updateReceive(form.value) : addReceive(form.value)
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
  proxy.$modal.confirm('是否确认删除资产领用编号为"' + _ids + '"的数据项？').then(function () {
    return delReceive(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

function handleExport() {
  proxy.download('asset/receive/export', {
    ...queryParams.value
  }, `receive_${new Date().getTime()}.xlsx`)
}

// 初始化加载
onMounted(async () => {
  // 加载选项数据
  await Promise.all([
    loadUserOptions(),
    loadDeptOptions()
  ])

  // 加载列表和资产选项
  getList()
  loadAssetOptions()
})
</script>
