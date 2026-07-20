<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="分类编码" prop="categoryCode">
        <el-select
            v-model="queryParams.categoryCode"
            placeholder="请选择分类编码"
            clearable
            @keyup.enter="handleQuery"
        >
          <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.categoryCode"
              :value="item.categoryCode"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="分类名称" prop="categoryName">
        <el-select
            v-model="queryParams.categoryName"
            placeholder="请选择分类名称"
            clearable
            @keyup.enter="handleQuery"
        >
          <el-option
              v-for="item in categoryList"
              :key="item.id"
              :label="item.categoryName"
              :value="item.categoryName"
          />
        </el-select>
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
            v-hasPermi="['asset:category:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="info"
            plain
            @click="toggleExpandAll"
        >展开/折叠
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
        v-if="refreshTable"
        v-loading="loading"
        :data="categoryList"
        row-key="id"
        :default-expand-all="isExpandAll"
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
        border
    >
      <el-table-column label="分类编码" prop="categoryCode">
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.categoryCode || '-' }}</a>
        </template>
      </el-table-column>
      <el-table-column label="分类名称" prop="categoryName" show-overflow-tooltip
      />
      <!--      <el-table-column label="父级分类名称"  align="center" prop="parentId">-->
      <!--        <template #default="scope">-->
      <!--          <span>{{categoryList.find(c => c.id === scope.row.parentId)?.categoryName || '-'}}</span>-->
      <!--        </template>-->
      <!--      </el-table-column>-->
      <el-table-column align="center" label="层级" prop="level" show-overflow-tooltip/>
      <el-table-column label="排序" align="center" prop="sortOrder" show-overflow-tooltip
      />
      <el-table-column align="right" label="折旧年限 (单位：年)" prop="depreciationYears" show-overflow-tooltip
      />
      <el-table-column label="折旧方法" prop="depreciationMethod">
        <template #default="scope">
          <dict-tag :options="depreciation_method" :value="scope.row.depreciationMethod"/>
        </template>
      </el-table-column>
      <el-table-column align="right" label="残值率 (单位：%)" prop="salvageRate" show-overflow-tooltip
      />
      <el-table-column label="备注" prop="remark" show-overflow-tooltip/>
      <el-table-column label="操作" align="center" width="140" fixed="right" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" @click="handleUpdate(scope.row)" v-hasPermi="['asset:category:edit']">修改
          </el-button>
          <el-button link type="primary" @click="handleAdd(scope.row)" v-hasPermi="['asset:category:add']">新增
          </el-button>
          <el-button link type="primary" @click="handleDelete(scope.row)" v-hasPermi="['asset:category:remove']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改资产分类对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form ref="categoryRef" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="分类编码" prop="categoryCode">
              <el-input v-model="form.categoryCode" placeholder="请输入分类编码（如：ZC001）"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类名称" prop="categoryName">
              <el-input v-model="form.categoryName" placeholder="请输入分类名称"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="父分类" prop="parentId">
              <el-tree-select
                  v-model="form.parentId"
                  :data="categoryOptions"
                  :props="{ value: 'id', label: 'categoryName', children: 'children' }"
                  value-key="id"
                  placeholder="请选择父分类"
                  check-strictly
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="层级" prop="level">
              <el-input-number v-model="form.level" :min="1" style="width: 100%" disabled/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="折旧年限 (年)" prop="depreciationYears">
              <el-input-number v-model="form.depreciationYears" :min="1" style="width: 100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="折旧方法" prop="depreciationMethod">
              <el-select v-model="form.depreciationMethod" placeholder="请选择折旧方法" style="width: 100%">
                <el-option
                    v-for="dict in depreciation_method"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="残值率 (%)" prop="salvageRate">
              <el-input-number v-model="form.salvageRate" :min="0" :max="100" :precision="2" style="width: 100%"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
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
  </div>
</template>

<script setup lang="ts" name="Category">
import {getCurrentInstance, nextTick, reactive, ref, toRefs} from 'vue'
import {addCategory, delCategory, getCategory, listCategory, updateCategory} from "@/api/asset/category"
import type {Category, CategoryQueryParams} from "@/types/api/asset/category"
import type {TreeSelect} from '@/types/api/common'

const {proxy} = getCurrentInstance()
const {depreciation_method} = proxy.useDict('depreciation_method')

// 设置分页大小


const categoryList = ref<any[]>([])
const categoryOptions = ref<TreeSelect[]>([])
const open = ref<boolean>(false)
const loading = ref<boolean>(true)
const showSearch = ref<boolean>(true)
const title = ref<string>("")
const isExpandAll = ref<boolean>(true)
const refreshTable = ref<boolean>(true)

const data = reactive({
  form: {} as Category,
  queryParams: {
    categoryCode: undefined,
    categoryName: undefined,
    parentId: undefined,
  } as CategoryQueryParams,
  rules: {
    categoryCode: [
      {required: true, message: "分类编码不能为空", trigger: "blur"}
    ],
    categoryName: [
      {required: true, message: "分类名称不能为空", trigger: "blur"}
    ],
  }
})

const {queryParams, form, rules} = toRefs(data)


/** 查询资产分类列表 */
function getList() {
  loading.value = true
  listCategory(queryParams.value).then(response => {
    categoryList.value = proxy.handleTree(response.data, "id", "parentId")
    loading.value = false
  })
}

function getTreeselect() {
  listCategory().then(response => {
    categoryOptions.value = []
    const data = {id: 0, categoryName: '顶级节点', children: []}
    data.children = proxy.handleTree(response.data, "id", "parentId")
    categoryOptions.value.push(data)
  })
}

function handleQuery() {
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    id: null,
    categoryCode: null,
    categoryName: null,
    parentId: null,
    level: null,
    sortOrder: null,
    depreciationYears: null,
    depreciationMethod: null,
    salvageRate: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null
  }
  proxy.resetForm("categoryRef")
}

function handleAdd(row: Category) {
  reset()
  getTreeselect()
  if (row != null && row.id) {
    form.value.parentId = row.id
  } else {
    form.value.parentId = 0
  }
  open.value = true
  title.value = "添加资产分类"
}

function toggleExpandAll() {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  nextTick(() => {
    refreshTable.value = true
  })
}

async function handleUpdate(row: Category) {
  reset()
  await getTreeselect()
  if (row != null) {
    form.value.parentId = row.parentId
  }
  getCategory(row.id!).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改资产分类"
  })
}

function submitForm() {
  proxy.$refs["categoryRef"].validate((valid: boolean) => {
    if (valid) {
      const submitData = form.value.id != null ? updateCategory(form.value) : addCategory(form.value)
      submitData.then(() => {
        proxy.$modal.msgSuccess(form.value.id != null ? "修改成功" : "新增成功")
        open.value = false
        getList()
      })
    }
  })
}

function handleDelete(row: Category) {
  proxy.$modal.confirm('是否确认删除资产分类编码为"' + row.categoryCode + '"的数据项？').then(function () {
    return delCategory(row.id!)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {
  })
}

getList()
</script>

<style scoped lang="scss">
</style>