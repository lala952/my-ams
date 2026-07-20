<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="资产编码" prop="assetCode">
        <el-input
            v-model="queryParams.assetCode"
            placeholder="请输入资产编码"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="资产名称" prop="assetName">
        <el-input
            v-model="queryParams.assetName"
            placeholder="请输入资产名称"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="资产类型" prop="assetType">
        <el-select v-model="queryParams.assetType" placeholder="请选择" clearable style="width: 180px">
          <el-option v-for="dict in asset_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
        </el-select>
      </el-form-item>
      <el-form-item label="资产状态" prop="assetStatus">
        <el-select v-model="queryParams.assetStatus" placeholder="请选择" clearable style="width: 180px">
          <el-option v-for="dict in asset_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
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
        <el-button type="primary" plain @click="handleAdd" v-hasPermi="['asset:assets:add']" disabled>新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :disabled="single" @click="handleUpdate"
                   v-hasPermi="['asset:assets:edit']" disabled>修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain :disabled="multiple" @click="handleDelete"
                   v-hasPermi="['asset:assets:remove']" disabled>删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain @click="handleExport" v-hasPermi="['asset:assets:export']" disabled>
          导出
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain :disabled="multiple" @click="handleBatchPrint"
                   v-hasPermi="['asset:assets:query']" disabled>批量打印标签
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain @click="handleExportStickers" v-hasPermi="['asset:assets:export']" disabled>
          导出条形码贴纸
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="assetsList" @selection-change="handleSelectionChange" border>
      <el-table-column label="序号" type="index" width="60" align="center" fixed="left"/>

      <el-table-column label="资产编码" prop="assetCode" fixed="left"
                       show-overflow-tooltip>
        <template #default="scope">
          <a class="link-type" style="cursor:pointer" @click="handleView(scope.row)">{{ scope.row.assetCode }}</a>
        </template>
      </el-table-column>

      <el-table-column label="资产名称" prop="assetName"  show-overflow-tooltip>
        <template #default="scope">{{scope.row.assetName || "-" }}</template>
      </el-table-column>

      <el-table-column label="资产分类" prop="categoryId"  show-overflow-tooltip>
        <template #default="scope">{{ getCategoryName(scope.row.categoryId) }}</template>
      </el-table-column>

      <el-table-column label="资产状态" prop="assetStatus"  show-overflow-tooltip>
        <template #default="scope">
          <dict-tag :options="asset_status" :value="scope.row.assetStatus"/>
        </template>
      </el-table-column>

      <el-table-column label="使用部门" prop="deptId"  show-overflow-tooltip>
        <template #default="scope">{{ getDeptName(scope.row.deptId) }}</template>
      </el-table-column>

      <el-table-column label="使用人" prop="userId"  show-overflow-tooltip>
        <template #default="scope">{{ getUserName(scope.row.userId) }}</template>
      </el-table-column>

      <el-table-column label="存放位置" prop="location"  show-overflow-tooltip>
        <template #default="scope">{{ scope.row.location || "-" }}</template>
      </el-table-column>

      <el-table-column label="原值" prop="originalValue" align="right"  show-overflow-tooltip>
        <template #default="scope">{{ formatMoney(scope.row.originalValue) }}</template>
      </el-table-column>

      <el-table-column label="净值" prop="netValue" align="right"  show-overflow-tooltip>
        <template #default="scope">{{ formatMoney(scope.row.netValue) }}</template>
      </el-table-column>

      <!-- 操作列：操作列通常需要固定宽度 -->
      <el-table-column label="操作" align="center" width="230" fixed="right">
        <template #default="scope">
          <el-button-group>
            <el-button link type="primary" @click="handleView(scope.row)">详情</el-button>
            <el-button link type="primary" @click="handleQRCode(scope.row)">二维码</el-button>
            <el-button link type="primary" @click="handleBarcode(scope.row)">条码</el-button>
            <el-button link type="primary" @click="handleUpdate(scope.row)" >修改</el-button>
            <el-button link type="primary" @click="handleDelete(scope.row)" >删除</el-button>
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

    <!-- 详情抽屉 -->
    <el-drawer v-model="drawerVisible" size="85%"
               >
      <!-- 基本信息 -->
      <h4 class="section-title">基本信息</h4>

      <el-descriptions :column="3" border size="default">
        <el-descriptions-item label="资产编码">{{ viewData.assetCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ viewData.assetName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资产分类">{{ getCategoryName(viewData.categoryId) }}</el-descriptions-item>
        <el-descriptions-item label="资产类型">
          <dict-tag :options="asset_type" :value="viewData.assetType"/>
        </el-descriptions-item>
        <el-descriptions-item label="资产来源">
          <dict-tag :options="asset_source" :value="viewData.assetSource"/>
        </el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ viewData.model || '-' }}</el-descriptions-item>
        <el-descriptions-item label="品牌">{{ viewData.brand || '-' }}</el-descriptions-item>
        <el-descriptions-item label="序列号">{{ viewData.serialNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ viewData.quantity || 0 }}</el-descriptions-item>
        <el-descriptions-item label="单位">
          <dict-tag :options="measure_unit" :value="viewData.unit"/>
        </el-descriptions-item>
        <el-descriptions-item label="存放位置" :span="2">{{ viewData.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 财务信息 -->
      <h4 class="section-title">财务信息</h4>
      <el-descriptions :column="3" border size="default">
        <el-descriptions-item label="原值">{{ formatMoney(viewData.originalValue) }}</el-descriptions-item>
        <el-descriptions-item label="净值">{{ formatMoney(viewData.netValue) }}</el-descriptions-item>
        <el-descriptions-item label="累计折旧">{{
            formatMoney(viewData.accumulatedDepreciation)
          }}
        </el-descriptions-item>
        <el-descriptions-item label="残值">{{ formatMoney(viewData.salvageValue) }}</el-descriptions-item>
        <el-descriptions-item label="折旧方法">
          <dict-tag :options="depreciation_method" :value="viewData.depreciationMethod"/>
        </el-descriptions-item>
        <el-descriptions-item label="折旧率">{{
            viewData.depreciationRate ? viewData.depreciationRate + '%' : '-'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="采购价格">{{ formatMoney(viewData.purchasePrice) }}</el-descriptions-item>
        <el-descriptions-item label="购置日期">{{
            parseTime(viewData.purchaseDate, '{y}-{m}-{d}') || '-'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="开始折旧日期">
          {{ parseTime(viewData.depreciationStartDate, '{y}-{m}-{d}') || '-' }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 供应商与采购 -->
      <h4 class="section-title">供应商与采购</h4>
      <el-descriptions :column="3" border size="default">
        <el-descriptions-item label="供应商">{{ getSupplierName(viewData.supplierId) }}</el-descriptions-item>
        <el-descriptions-item label="生产日期">{{
            parseTime(viewData.manufactureDate, '{y}-{m}-{d}') || '-'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="保修截止日期">{{
            parseTime(viewData.warrantyExpireDate, '{y}-{m}-{d}') || '-'
          }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 使用信息 -->
      <h4 class="section-title">使用信息</h4>
      <el-descriptions :column="3" border size="default">
        <el-descriptions-item label="使用部门">{{ getDeptName(viewData.deptId) }}</el-descriptions-item>
        <el-descriptions-item label="使用人">{{ getUserName(viewData.userId) }}</el-descriptions-item>
        <el-descriptions-item label="开始使用日期">{{
            parseTime(viewData.startUseDate, '{y}-{m}-{d}') || '-'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="资产状态">
          <dict-tag :options="asset_status" :value="viewData.assetStatus"/>
        </el-descriptions-item>
        <el-descriptions-item label="报废日期">{{
            parseTime(viewData.scrapDate, '{y}-{m}-{d}') || '-'
          }}
        </el-descriptions-item>
      </el-descriptions>

      <!-- 其他信息 -->
      <h4 class="section-title">其他信息</h4>
      <el-descriptions :column="3" border size="default">
        <el-descriptions-item label="条码">{{ viewData.barcode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="RFID标签">{{ viewData.rfidTag || '-' }}</el-descriptions-item>
        <el-descriptions-item label="主图片" :span="2">
          <image-preview v-if="viewData.mainImage" :src="viewData.mainImage" :width="80" :height="80"/>
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-drawer>

    <!-- 二维码弹窗 -->
    <el-dialog v-model="qrcodeVisible" title="资产二维码" width="450px" append-to-body @closed="closeQRCodeDialog">
      <div class="qrcode-container" id="qrcode-print-area">
        <div class="qrcode-title">
          <h3>{{ currentAsset.assetName || '-' }}</h3>
          <p>资产编码：<a class="link-type" @click="handleView(currentAsset)">{{ currentAsset.assetCode || '-' }}</a></p>
        </div>
        <div class="qrcode-image">
          <img v-if="qrcodeImageUrl" :src="qrcodeImageUrl" style="width: 200px; height: 200px;"/>
          <el-icon v-else class="is-loading" :size="40">
            <Loading/>
          </el-icon>
        </div>
        <div class="qrcode-info"><p>手机扫码查看资产信息</p></div>
      </div>
      <template #footer>
        <el-button type="primary" @click="printQRCode">打印</el-button>
        <el-button @click="downloadQRCodeImage">下载</el-button>
        <el-button @click="showJsonData = true">查看JSON数据</el-button>
        <el-button @click="qrcodeVisible = false">关 闭</el-button>
      </template>
    </el-dialog>

    <!-- JSON数据弹窗 -->
    <el-dialog v-model="showJsonData" title="资产JSON数据" width="600px" append-to-body>
      <div class="json-container">
        <pre>{{ jsonData }}</pre>
      </div>
      <template #footer>
        <el-button @click="showJsonData = false">关 闭</el-button>
      </template>
    </el-dialog>

    <!-- 条码弹窗 -->
    <el-dialog v-model="barcodeVisible" title="资产条码" width="500px" append-to-body @closed="closeBarcodeDialog">
      <div class="barcode-container" id="barcode-print-area">
        <div class="barcode-title">
          <h3>{{ currentAsset.assetName || '-' }}</h3>
          <p>资产编码：<a class="link-type" @click="handleView(currentAsset)">{{ currentAsset.assetCode || '-' }}</a></p>
        </div>
        <div class="barcode-image">
          <img v-if="barcodeImageUrl" :src="barcodeImageUrl" style="height: 80px;"/>
          <el-icon v-else class="is-loading" :size="40">
            <Loading/>
          </el-icon>
        </div>
        <div class="barcode-info">
          <p>资产编码：<a class="link-type" @click="handleView(currentAsset)">{{ currentAsset.assetCode || '-' }}</a></p>
          <p>资产名称：{{ currentAsset.assetName || '-' }}</p>
          <p>存放位置：{{ currentAsset.location || '-' }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="barcodeVisible = false">关 闭</el-button>
        <el-button type="primary" @click="printBarcode">打印</el-button>
        <el-button @click="downloadBarcodeImage">下载</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="MyAssets">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from 'vue'
import {Loading} from '@element-plus/icons-vue'
import {formatMoney} from '@/utils/asset'
import {useAssetOptions} from '@/composables/asset/useAssetOptions'

//  API 接口导入
import {downloadCode128, downloadQRCode, getMyAsset, listMyAssets, previewCode128, previewQRCode} from "@/api/asset/my"

//  字典数据
const {proxy} = getCurrentInstance()
const {depreciation_method, asset_type, asset_source, measure_unit, asset_status} = proxy.useDict(
    'depreciation_method', 'asset_type', 'asset_source', 'measure_unit', 'asset_status'
)

//  响应式数据 
const assetsList = ref([])
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
const qrcodeVisible = ref(false)
const barcodeVisible = ref(false)
const showJsonData = ref(false)
const currentAsset = ref({})
const qrcodeImageUrl = ref('')
const barcodeImageUrl = ref('')
const jsonData = ref('')

//  表单数据 
const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    assetCode: undefined,
    assetName: undefined,
    categoryId: undefined,
    assetType: undefined,
    assetSource: undefined,
    supplierId: undefined,
    assetStatus: undefined,
    location: undefined,
  },
  rules: {
    assetName: [{required: true, message: "资产名称不能为空", trigger: "blur"}],
    assetType: [{required: true, message: "资产类型不能为空", trigger: "change"}],
    assetSource: [{required: true, message: "资产来源不能为空", trigger: "change"}],
    quantity: [
      {required: true, message: "数量不能为空", trigger: "blur"},
      {type: 'number', min: 1, message: "数量必须大于等于1", trigger: "blur"}
    ],
    unit: [{required: true, message: "单位不能为空", trigger: "change"}],
    originalValue: [
      {required: true, message: "原值不能为空", trigger: "blur"},
      {type: 'number', min: 0, message: "原值不能为负数", trigger: "blur"}
    ],
    purchasePrice: [
      {required: true, message: "采购价格不能为空", trigger: "blur"},
      {type: 'number', min: 0, message: "采购价格不能为负数", trigger: "blur"}
    ],
    supplierId: [{required: true, message: "供应商不能为空", trigger: "change"}],
    purchaseDate: [{required: true, message: "购置日期不能为空", trigger: "change"}],
    assetStatus: [{required: true, message: "资产状态不能为空", trigger: "change"}],
    model: [{required: false, message: "请输入规格型号", trigger: "blur"}],
    brand: [{required: false, message: "请输入品牌", trigger: "blur"}],
    location: [{required: false, message: "请输入存放位置", trigger: "blur"}],
    salvageValue: [{required: false}, {type: 'number', min: 0, message: "残值不能为负数", trigger: "blur"}],
    depreciationMethod: [{required: false, message: "请选择折旧方法", trigger: "change"}],
    deptId: [{required: false, message: "请选择使用部门", trigger: "change"}],
    userId: [{required: false, message: "请选择使用人", trigger: "change"}],
    startUseDate: [{required: false, message: "请选择开始使用日期", trigger: "change"}],
    barcode: [{required: false, message: "请输入条码", trigger: "blur"}]
  }
})

const {queryParams, form, rules} = toRefs(data)

//  辅助函数 
function parseTime(time, pattern) {
  return proxy.parseTime(time, pattern)
}

// 使用 Composable 加载公共选项数据和名称映射工具
const {
  userOptions,
  deptOptions,
  categoryOptions,
  supplierOptions,
  getUserName,
  getDeptName,
  getCategoryName,
  getSupplierName,
  loadAll: loadOptions
} = useAssetOptions();

//  CRUD 操作 
function getList() {
  loading.value = true
  listMyAssets(queryParams.value).then(response => {
    assetsList.value = response.rows
    total.value = response.total
    loading.value = false
  }).catch(() => {
    assetsList.value = []
    total.value = 0
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

function reset() {
  form.value = {
    id: null, assetCode: null, assetName: null, categoryId: null,
    assetType: null, assetSource: null, model: null, brand: null,
    serialNumber: null, quantity: null, unit: null, originalValue: null,
    salvageValue: null, depreciationMethod: null, depreciationRate: null,
    accumulatedDepreciation: null, netValue: null, depreciationStartDate: null,
    supplierId: null, purchaseDate: null, purchasePrice: null,
    warrantyExpireDate: null, deptId: null, userId: null, location: null,
    assetStatus: null, barcode: null, rfidTag: null, manufactureDate: null,
    startUseDate: null, scrapDate: null, mainImage: null, remark: null,
    createBy: null, createTime: null, updateBy: null,
    updateTime: null, delFlag: null
  }
  proxy.$refs["assetsRef"]?.resetFields()
}

function handleAdd() {
  proxy.$modal.msgWarning('名下资产不支持新增操作')
}

function handleView(row) {
  viewData.value = row
  drawerVisible.value = true
}

function handleUpdate(row) {
  proxy.$modal.msgWarning('名下资产不支持修改操作')
}

function submitForm() {
  proxy.$modal.msgWarning('名下资产不支持修改操作')
}

function cancel() {
  open.value = false
  reset()
}

function handleDelete(row) {
  proxy.$modal.msgWarning('名下资产不支持删除操作')
}

function handleExport() {
  proxy.$modal.msgWarning('名下资产不支持导出操作')
}

//  二维码/条码相关 
function handleQRCode(row) {
  currentAsset.value = row
  qrcodeVisible.value = true
  qrcodeImageUrl.value = ''

  previewQRCode(row.id).then(blob => {
    if (qrcodeImageUrl.value) URL.revokeObjectURL(qrcodeImageUrl.value)
    qrcodeImageUrl.value = URL.createObjectURL(blob)
  }).catch(error => {
    console.error('二维码生成失败:', error)
    proxy.$modal.msgError('二维码生成失败')
    qrcodeVisible.value = false
  })

  getMyAsset(row.id).then(res => {
    if (res.code === 200 && res.data) {
      const {createBy, createTime, updateBy, updateTime, delFlag, ...businessData} = res.data
      jsonData.value = JSON.stringify(businessData, null, 2)
    }
  })
}

function handleBarcode(row) {
  currentAsset.value = row
  barcodeVisible.value = true
  barcodeImageUrl.value = ''

  previewCode128(row.assetCode).then(blob => {
    if (barcodeImageUrl.value) URL.revokeObjectURL(barcodeImageUrl.value)
    barcodeImageUrl.value = URL.createObjectURL(blob)
  }).catch(error => {
    console.error('条码生成失败:', error)
    proxy.$modal.msgError('条码生成失败')
    barcodeVisible.value = false
  })
}

function downloadQRCodeImage() {
  downloadQRCode(currentAsset.value.id).then(blob => {
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${currentAsset.value.assetCode}.png`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
  }).catch(() => proxy.$modal.msgError('二维码下载失败'))
}

function downloadBarcodeImage() {
  downloadCode128(currentAsset.value.assetCode).then(blob => {
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${currentAsset.value.assetCode}.png`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
  }).catch(() => proxy.$modal.msgError('条码下载失败'))
}

function closeQRCodeDialog() {
  if (qrcodeImageUrl.value) URL.revokeObjectURL(qrcodeImageUrl.value)
  qrcodeImageUrl.value = ''
  jsonData.value = ''
}

function closeBarcodeDialog() {
  if (barcodeImageUrl.value) URL.revokeObjectURL(barcodeImageUrl.value)
  barcodeImageUrl.value = ''
}

function printQRCode() {
  const printContent = document.getElementById('qrcode-print-area')
  if (!printContent) return
  const printWindow = window.open('', '_blank')
  printWindow.document.write(`
    <html><head><title>资产二维码</title>
    <style>body{margin:0;padding:20px;display:flex;justify-content:center;align-items:center;min-height:100vh}
    .qrcode-container{text-align:center;padding:20px}
    .qrcode-title h3{margin:0 0 10px;font-size:18px;color:#303133}
    .qrcode-title p{margin:0 0 20px;font-size:14px;color:#909399}
    .qrcode-image{margin:20px 0;display:flex;justify-content:center}
    .qrcode-info p{margin:0;font-size:12px;color:#c0c4cc}</style>
    </head><body>${printContent.innerHTML}</body></html>
  `)
  printWindow.document.close()
  printWindow.focus()
  setTimeout(() => {
    printWindow.print();
    printWindow.close()
  }, 250)
}

function printBarcode() {
  const printContent = document.getElementById('barcode-print-area')
  if (!printContent) return
  const printWindow = window.open('', '_blank')
  printWindow.document.write(`
    <html><head><title>资产条码</title>
    <style>body{margin:0;padding:20px;display:flex;justify-content:center;align-items:center;min-height:100vh}
    .barcode-container{text-align:center;padding:20px}
    .barcode-title h3{margin:0 0 10px;font-size:18px;color:#303133}
    .barcode-title p{margin:0 0 20px;font-size:14px;color:#909399}
    .barcode-image{margin:20px 0;display:flex;justify-content:center}
    .barcode-info p{margin:5px 0;font-size:12px;color:#606266}</style>
    </head><body>${printContent.innerHTML}</body></html>
  `)
  printWindow.document.close()
  printWindow.focus()
  setTimeout(() => {
    printWindow.print();
    printWindow.close()
  }, 250)
}

//  批量操作 
function handleBatchPrint() {
  if (ids.value.length === 0) {
    proxy.$modal.msgWarning('请选择要打印的资产')
    return
  }
  proxy.$modal.msgWarning('名下资产不支持批量打印操作')
}

// 批量操作 - 导出条形码贴纸
function handleExportStickers() {
  if (ids.value.length === 0) {
    proxy.$modal.msgWarning('请选择要导出的资产')
    return
  }
  proxy.$modal.msgWarning('名下资产不支持导出操作')
}

onMounted(async () => {
  // 加载选项数据
  await loadOptions()

  // 加载列表数据
  getList()
})
</script>

<style scoped lang="scss">

.qrcode-container, .barcode-container {
  text-align: center;
  padding: 5px 10px;

  .qrcode-title, .barcode-title {
    margin-bottom: 20px;

    h3 {
      margin: 0 0 10px 0;
      font-size: 18px;
      font-weight: 600;
      color: #303133;
    }

    p {
      margin: 0;
      font-size: 14px;
      color: #909399;
    }
  }

  .qrcode-image, .barcode-image {
    margin: 20px 0;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 200px;
  }

  .qrcode-info p, .barcode-info p {
    margin: 5px 0;
    font-size: 12px;
    color: #606266;
  }
}

.json-container {
  max-height: 400px;
  overflow: auto;

  pre {
    margin: 0;
    padding: 12px;
    background: #f5f7fa;
    border-radius: 4px;
    font-size: 12px;
    white-space: pre-wrap;
    word-wrap: break-word;
  }
}
</style>
