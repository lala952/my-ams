<template>
  <div class="asset-table-wrapper">
    <!-- 工具栏 -->
    <div class="table-toolbar" v-if="showToolbar">
      <div class="toolbar-left">
        <span v-if="showTotalValue" class="total-value-label">
          总价值
          <span class="total-value">{{ formatMoney(totalValue) }}</span> 元
        </span>
        <slot name="toolbar-left"></slot>
      </div>

      <div class="toolbar-right">
        <el-input
            v-if="searchable"
            v-model="localSearchKeyword"
            placeholder="请输入 资产编码/名称 进行搜索"
            clearable
            size="small"
            style="width: 250px"
            @input="handleLocalSearch"
        >
          <template #suffix>
            <el-icon>
              <Search/>
            </el-icon>
          </template>
        </el-input>
        <slot name="toolbar-right"></slot>
        <!-- 内置选择资产按钮 -->
        <el-button
            v-if="enableAssetSelector"
            size="small"
            type="primary"
            @click="openAssetDrawer"
        >
          选择资产
        </el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <el-table
        ref="tableRef"
        v-loading="loading"
        :data="displayData"
        height="480"
        :row-key="rowKey"
        @selection-change="handleSelectionChange"
        border
    >
      <el-table-column align="center" type="selection" width="55" v-if="showSelection"/>
      <el-table-column align="center" label="序号" type="index" width="60"/>
      <el-table-column label="资产编码" prop="assetCode" sortable show-overflow-tooltip>
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{ scope.row.assetCode }}</a>
        </template>
      </el-table-column>
      <el-table-column label="资产名称" prop="assetName" show-overflow-tooltip sortable/>
      <el-table-column label="资产分类" prop="categoryId" sortable show-overflow-tooltip>
        <template #default="scope">{{ getCategoryName(scope.row.categoryId) }}</template>
      </el-table-column>
      <el-table-column label="资产状态" prop="assetStatus" sortable show-overflow-tooltip>
        <template #default="scope">
          <dict-tag :options="asset_status" :value="scope.row.assetStatus"/>
        </template>
      </el-table-column>
      <el-table-column label="使用人" prop="userId" sortable show-overflow-tooltip>
        <template #default="scope">{{ getUserName(scope.row.userId) }}</template>
      </el-table-column>
      <el-table-column label="使用部门" prop="deptId" sortable show-overflow-tooltip>
        <template #default="scope">{{ getDeptName(scope.row.deptId) }}</template>
      </el-table-column>
      <el-table-column label="存放位置" prop="location" show-overflow-tooltip sortable/>
      <el-table-column label="原值" prop="originalValue" align="right" sortable show-overflow-tooltip>
        <template #default="scope">{{ formatMoney(scope.row.originalValue) }} 元</template>
      </el-table-column>
      <el-table-column label="净值" prop="netValue" align="right" sortable show-overflow-tooltip>
        <template #default="scope">{{ formatMoney(scope.row.netValue) }} 元</template>
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column :width="150" align="center" label="操作" v-if="showActions">
        <template #default="scope">
          <el-button link type="primary" @click="handleView(scope.row)">详情</el-button>
          <el-button v-if="editable" link type="primary" @click="handleEdit(scope.row)"
                     :disabled="disabledEditAndRemove">修改
          </el-button>
          <el-button v-if="editable" link type="primary" @click="handleRemove(scope.row, scope.$index)"
                     :disabled="disabledEditAndRemove">移除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
        :total="total"
        :page="currentPage"
        :limit="pageSize"
        v-if="showPagination"
        @update:page="handlePageChange"
        @update:limit="handleLimitChange"
        @pagination="handlePagination"
    />

    <!-- 选择资产抽屉 -->
    <el-drawer
        v-model="assetDrawerVisible"
        append-to-body
        size="85%"
        title="选择资产"
        @close="handleDrawerClose"
    >
      <div class="asset-selector-wrapper">
        <el-form :inline="true" :model="assetQueryParams">
          <el-form-item label="资产编码">
            <el-input
                v-model="assetQueryParams.assetCode"
                clearable
                placeholder="请输入资产编码"
                style="width: 180px"
            />
          </el-form-item>
          <el-form-item label="资产名称">
            <el-input
                v-model="assetQueryParams.assetName"
                clearable
                placeholder="请输入资产名称"
                style="width: 180px"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">清空</el-button>
          </el-form-item>
        </el-form>

        <el-table
            ref="selectorTableRef"
            v-loading="assetLoading"
            :data="selectableAssetsList"
            row-key="id"
            @selection-change="handleSelectableAssetChange"
            border
            height="480"
        >
          <el-table-column align="center" type="selection" width="55"/>
          <el-table-column align="center" label="序号" type="index" width="60"/>
          <el-table-column label="资产编码" prop="assetCode" show-overflow-tooltip>
            <template #default="scope">
              <a class="link-type" @click="handleView(scope.row)">{{ scope.row.assetCode }}</a>
            </template>
          </el-table-column>
          <el-table-column label="资产名称" prop="assetName" show-overflow-tooltip/>
          <el-table-column label="资产分类" prop="categoryId" show-overflow-tooltip>
            <template #default="scope">{{ getCategoryName(scope.row.categoryId) }}</template>
          </el-table-column>
          <el-table-column label="资产状态" prop="assetStatus" show-overflow-tooltip>
            <template #default="scope">
              <dict-tag :options="asset_status" :value="scope.row.assetStatus"/>
            </template>
          </el-table-column>
          <el-table-column label="使用人" prop="userId" show-overflow-tooltip>
            <template #default="scope">{{ getUserName(scope.row.userId) }}</template>
          </el-table-column>
          <el-table-column label="使用部门" prop="deptId" show-overflow-tooltip>
            <template #default="scope">{{ getDeptName(scope.row.deptId) }}</template>
          </el-table-column>
          <el-table-column label="存放位置" prop="location" show-overflow-tooltip/>
          <el-table-column label="原值" prop="originalValue" align="right" show-overflow-tooltip>
            <template #default="scope">{{ formatMoney(scope.row.originalValue) }} 元</template>
          </el-table-column>
          <el-table-column label="净值" prop="netValue" align="right" show-overflow-tooltip>
            <template #default="scope">{{ formatMoney(scope.row.netValue) }} 元</template>
          </el-table-column>
        </el-table>

        <pagination
            :total="assetSelectTotal"
            :page="assetQueryParams.pageNum"
            :limit="assetQueryParams.pageSize"
            @update:page="handleAssetPageChange"
            @update:limit="handleAssetLimitChange"
        />
      </div>

      <template #footer>
        <el-button @click="assetDrawerVisible = false">取消</el-button>
        <el-button
            :disabled="ids.length === 0"
            type="primary"
            @click="confirmAssetSelect"
        >
          确定选择<span v-if="ids.length > 0">
            ({{ ids.length }})</span>
        </el-button>
      </template>
    </el-drawer>

    <!--  内置编辑弹窗 -->
    <el-dialog
        v-model="open"
        :title="title"
        append-to-body
        width="75%"
    >
      <el-form ref="formRef" :model="form" label-width="120px">
        <el-divider content-position="left"><span style="font-weight: bold">基本信息</span></el-divider>
        <el-row>
          <el-col :span="6">
            <el-form-item label="资产编码" prop="assetCode">
              <el-input v-model="form.assetCode" disabled/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="资产名称" prop="assetName">
              <el-input v-model="form.assetName" placeholder="请输入资产名称"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="资产分类" prop="categoryId">
              <el-select v-model="form.categoryId" clearable placeholder="请选择" style="width: 100%">
                <el-option v-for="cat in categoryOptions" :key="cat.id" :label="cat.categoryName" :value="cat.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="资产类型" prop="assetType">
              <el-select v-model="form.assetType" placeholder="请选择" style="width: 100%">
                <el-option v-for="item in asset_type" :key="item.value" :label="item.label" :value="item.value"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="资产来源" prop="assetSource">
              <el-select v-model="form.assetSource" placeholder="请选择" style="width: 100%">
                <el-option v-for="item in asset_source" :key="item.value" :label="item.label" :value="item.value"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="规格型号" prop="model">
              <el-input v-model="form.model" placeholder="请输入规格型号"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="form.brand" placeholder="请输入品牌"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="序列号" prop="serialNumber">
              <el-input v-model="form.serialNumber" placeholder="请输入序列号"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="单位" prop="unit">
              <el-select v-model="form.unit" placeholder="请选择" style="width: 100%">
                <el-option v-for="item in measure_unit" :key="item.value" :label="item.label" :value="item.value"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left"><span style="font-weight: bold">存放位置</span></el-divider>
        <el-row>
          <el-col :span="10">
            <el-form-item label="所在地区">
              <RegionSelector v-model="locationAreaCodes" @change="handleRegionChange"/>
            </el-form-item>
          </el-col>
          <el-col :span="14">
            <el-form-item label="详细位置" prop="locationDetail">
              <el-input v-model="form.locationDetail" placeholder="请输入资产存放详细位置" @input="updateFullLocation"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left"><span style="font-weight: bold">财务信息</span></el-divider>
        <el-row>
          <el-col :span="6">
            <el-form-item label="原值" prop="originalValue">
              <el-input-number v-model="form.originalValue" :min="0" :precision="2" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="残值" prop="salvageValue">
              <el-input-number v-model="form.salvageValue" :min="0" :precision="2" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="采购价格" prop="purchasePrice">
              <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="折旧方法" prop="depreciationMethod">
              <el-select v-model="form.depreciationMethod" placeholder="请选择" style="width: 100%">
                <el-option v-for="item in depreciation_method" :key="item.value" :label="item.label"
                           :value="item.value"/>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left"><span style="font-weight: bold">供应商与采购</span></el-divider>
        <el-row>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="form.supplierId" clearable filterable placeholder="请选择" style="width: 100%">
                <el-option v-for="sup in supplierOptions" :key="sup.id" :label="sup.supplierName" :value="sup.id"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="购置日期" prop="purchaseDate">
              <el-date-picker v-model="form.purchaseDate" placeholder="请选择购置日期" style="width: 100%"
                              type="date" value-format="YYYY-MM-DD"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left"><span style="font-weight: bold">使用信息</span></el-divider>
        <el-row>
          <el-col :span="6">
            <el-form-item label="使用部门" prop="deptId">
              <el-tree-select v-model="form.deptId" :data="deptOptions" :props="{ value: 'deptId', label: 'deptName' }"
                              check-strictly placeholder="请选择使用部门" style="width: 100%"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="使用人" prop="userId">
              <el-select v-model="form.userId" clearable filterable placeholder="请选择" style="width: 100%">
                <el-option v-for="user in userOptions" :key="user.userId" :label="user.nickName" :value="user.userId"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="开始使用日期" prop="startUseDate">
              <el-date-picker v-model="form.startUseDate" placeholder="请选择开始使用日期" style="width: 100%"
                              type="date" value-format="YYYY-MM-DD"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="保修截止日期" prop="warrantyExpireDate">
              <el-date-picker v-model="form.warrantyExpireDate" placeholder="请选择保修截止日期" style="width: 100%"
                              type="date" value-format="YYYY-MM-DD"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left"><span style="font-weight: bold">其他信息</span></el-divider>
        <el-row>
          <el-col :span="6">
            <el-form-item label="生产日期" prop="manufactureDate">
              <el-date-picker v-model="form.manufactureDate" placeholder="请选择生产日期" style="width: 100%"
                              type="date" value-format="YYYY-MM-DD"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="资产状态" prop="assetStatus">
              <el-select v-model="form.assetStatus" placeholder="请选择" style="width: 100%">
                <el-option v-for="item in asset_status" :key="item.value" :label="item.label" :value="item.value"/>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="条码" prop="barcode">
              <el-input v-model="form.barcode" placeholder="请输入条码"/>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="RFID标签" prop="rfidTag">
              <el-input v-model="form.rfidTag" placeholder="请输入RFID标签"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" :rows="1" maxlength="500" placeholder="请输入备注" show-word-limit
                        type="textarea"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>

    <!--  内置详情抽屉 -->
    <el-drawer v-model="viewVisible" append-to-body size="85%">
      <template #header><h3 style="margin:0">资产详情</h3></template>
      <h4 class="section-title">基本信息</h4>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="资产编码">{{ viewData.assetCode || '-' }}</el-descriptions-item>
        <el-descriptions-item :span="2" label="资产名称">{{ viewData.assetName || '-' }}</el-descriptions-item>
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
        <el-descriptions-item :span="3" label="存放位置">{{ viewData.location || '-' }}</el-descriptions-item>
      </el-descriptions>

      <h4 class="section-title">财务信息</h4>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="原值(元)">{{ formatMoney(viewData.originalValue) }}</el-descriptions-item>
        <el-descriptions-item label="采购价格(元)">{{ formatMoney(viewData.purchasePrice) }}</el-descriptions-item>
        <el-descriptions-item label="残值(元)">{{ formatMoney(viewData.salvageValue) }}</el-descriptions-item>
        <el-descriptions-item label="累计折旧(元)">{{
            formatMoney(viewData.accumulatedDepreciation)
          }}
        </el-descriptions-item>
        <el-descriptions-item label="净值(元)"><b>{{ formatMoney(viewData.netValue) }}</b></el-descriptions-item>
        <el-descriptions-item label="折旧方法">
          <dict-tag :options="depreciation_method" :value="viewData.depreciationMethod"/>
        </el-descriptions-item>
        <el-descriptions-item label="购置日期">{{
            parseTime(viewData.purchaseDate, '{y}-{m}-{d}') || '-'
          }}
        </el-descriptions-item>
      </el-descriptions>

      <h4 class="section-title">使用信息</h4>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="使用部门">{{ getDeptName(viewData.deptId) }}</el-descriptions-item>
        <el-descriptions-item label="使用人">{{ getUserName(viewData.userId) }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ viewData.quantity || 0 }}</el-descriptions-item>
        <el-descriptions-item label="单位">
          <dict-tag :options="measure_unit" :value="viewData.unit"/>
        </el-descriptions-item>
        <el-descriptions-item label="开始使用日期">{{
            parseTime(viewData.startUseDate, '{y}-{m}-{d}') || '-'
          }}
        </el-descriptions-item>
        <el-descriptions-item label="保修截止日期">{{
            parseTime(viewData.warrantyExpireDate, '{y}-{m}-{d}') || '-'
          }}
        </el-descriptions-item>
      </el-descriptions>

      <h4 class="section-title">备注</h4>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="备注">{{ viewData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <h4 class="section-title">资产主图</h4>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="主图">
          <image-preview :src="viewData.mainImage" :width="80" :height="80"/>
        </el-descriptions-item>
      </el-descriptions>
    </el-drawer>
  </div>
</template>

<script setup>
import {computed, getCurrentInstance, onMounted, reactive, ref} from "vue";
import {Search} from "@element-plus/icons-vue";
import {formatMoney} from "@/utils/asset";
import {parseTime} from "@/utils/ruoyi";
import {useAssetOptions} from "@/composables/asset/useAssetOptions";
import RegionSelector from '@/components/Asset/RegionSelector/index.vue'
import {getAssets, getAssetsByIds, listAssets} from "@/api/asset/assets";
import {codeToText} from "element-china-area-data";
import ImagePreview from "@/components/ImagePreview/index.vue";

const {proxy} = getCurrentInstance();
const {
  depreciation_method,
  asset_type,
  asset_source,
  measure_unit,
  asset_status
} = proxy.useDict('depreciation_method', 'asset_type', 'asset_source', 'measure_unit', 'asset_status');

//  使用组合式函数获取选项数据和映射方法
const {
  categoryOptions,
  supplierOptions,
  deptOptions,
  userOptions,
  loadAll: loadAllOptions,
  getCategoryName,
  getDeptName,
  getUserName
} = useAssetOptions();

// Props 定义
const props = defineProps({
  modelValue: {type: Array, default: () => []},
  loading: {type: Boolean, default: false},
  total: {type: Number, default: 0},
  currentPage: {type: Number, default: 1},
  pageSize: {type: Number, default: 10},
  showToolbar: {type: Boolean, default: true},
  showPagination: {type: Boolean, default: true},
  showTotalValue: {type: Boolean, default: false},
  showSelection: {type: Boolean, default: false},  // 是否显示选择列（用于父组件需要选择时）
  showActions: {type: Boolean, default: true},      // 是否显示操作列
  rowKey: {type: String, default: "id"},
  searchable: {type: Boolean, default: false},
  editable: {type: Boolean, default: true},
  disabledEditAndRemove: {type: Boolean, default: false},
  // 新增：是否启用资产选择器（内置）
  enableAssetSelector: {type: Boolean, default: false},
  // 新增：资产选择器的过滤条件
  assetSelectorFilter: {type: Object, default: () => ({})},
});

// Emits 定义
const emit = defineEmits([
  "update:modelValue",
  "selection-change",
  "search",
  "page-change",
  "pagination",
]);

// 内部状态
const localSearchKeyword = ref("");
let searchTimer = null;
const selectedRows = ref([]);
const tableRef = ref(null);

// 资产选择器相关
const assetDrawerVisible = ref(false);
const selectableAssetsList = ref([]);
const assetLoading = ref(false);
const assetSelectTotal = ref(0);
const ids = ref([]);
const selectorTableRef = ref(null);
const assetQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  assetCode: null,
  assetName: null,
  ...props.assetSelectorFilter,  // 合并外部过滤条件
});

//  编辑相关
const open = ref(false);
const title = ref("修改资产台账");
const editIndex = ref(-1);
const form = ref({});
const originalAsset = ref({});
const locationAreaCodes = ref([]);

//  查看详情相关
const viewVisible = ref(false);
const viewData = ref({});

//  计算属性：显示的数据（支持本地搜索过滤）
const displayData = computed(() => {
  if (!localSearchKeyword.value) return props.modelValue;
  const keyword = localSearchKeyword.value.toLowerCase();
  return props.modelValue.filter(item =>
      (item.assetCode && item.assetCode.toLowerCase().includes(keyword)) ||
      (item.assetName && item.assetName.toLowerCase().includes(keyword))
  );
});

// 计算总价值
const totalValue = computed(() => {
  const list = Array.isArray(props.modelValue) ? props.modelValue : []
  return list.reduce((sum, item) => sum + (Number(item.originalValue) || 0), 0);
});

//  地区选择联动
const handleRegionChange = () => updateFullLocation();
const updateFullLocation = () => {
  const areaText = buildAreaText(locationAreaCodes.value);
  const detail = form.value.locationDetail || '';
  form.value.location = areaText + (detail ? '' + detail : '');
};

const buildAreaText = (codes) => {
  if (!codes || codes.length === 0) return '';
  return codes.map(code => codeToText[code] || '').join('');
};

//  解析 location 为地区代码
const parseLocationToAreaCodes = (fullAddress) => {
  if (!fullAddress) {
    locationAreaCodes.value = [];
    form.value.locationDetail = '';
    return;
  }
  let remaining = fullAddress;
  const codes = [];
  for (const [code, name] of Object.entries(codeToText)) {
    if (remaining.startsWith(name)) {
      codes.push(Number(code));
      remaining = remaining.substring(name.length);
      break;
    }
  }
  for (const [code, name] of Object.entries(codeToText)) {
    if (remaining.startsWith(name) && code.length === 4) {
      codes.push(Number(code));
      remaining = remaining.substring(name.length);
      break;
    }
  }
  for (const [code, name] of Object.entries(codeToText)) {
    if (remaining.startsWith(name) && code.length === 6) {
      codes.push(Number(code));
      remaining = remaining.substring(name.length);
      break;
    }
  }
  locationAreaCodes.value = codes;
  form.value.locationDetail = remaining.trim();
};

//  处理选择变化
function handleSelectionChange(selection) {
  selectedRows.value = selection;
  emit("selection-change", selection);
}

//  本地搜索（防抖）
function handleLocalSearch() {
  if (searchTimer) clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    emit("search", localSearchKeyword.value);
  }, 300);
}

//  分页处理
function handlePageChange(page) {
  emit("page-change", {page, size: props.pageSize});
}

function handleLimitChange(limit) {
  emit("page-change", {page: 1, size: limit});
}

function handlePagination() {
  emit("pagination");
}

//  查看详情
function handleView(row) {
  viewData.value = row;
  viewVisible.value = true;
}

//  编辑资产
// 重置编辑表单
/** 表单重置 */
function reset() {
  form.value = {
    id: null,
    assetCode: null,
    assetName: null,
    categoryId: null,
    assetType: null,
    assetSource: null,
    model: null,
    brand: null,
    serialNumber: null,
    quantity: null,
    unit: null,
    originalValue: null,
    salvageValue: null,
    depreciationMethod: null,
    depreciationRate: null,
    accumulatedDepreciation: null,
    netValue: null,
    depreciationStartDate: null,
    supplierId: null,
    purchaseDate: null,
    purchasePrice: null,
    warrantyExpireDate: null,
    deptId: null,
    userId: null,
    location: null,
    longitude: null,
    latitude: null,
    assetStatus: null,
    barcode: null,
    rfidTag: null,
    manufactureDate: null,
    startUseDate: null,
    scrapDate: null,
    mainImage: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null,
  };
  proxy.resetForm("formRef")
}
async function handleEdit(row) {
  reset();
  getAssets(row.id).then((res) => {
    form.value = res.data;
    open.value = true;
    title.value = "修改资产台账";
  })
}

//  保存编辑
function saveEdit() {
  const newList = [...props.modelValue];
  const index = newList.findIndex(item => item.id === form.value.id);
  if (index !== -1) {
    newList[index] = form.value;
  }
  emit("update:modelValue", newList);

  open.value = false;
  proxy.$modal.msgSuccess("保存成功");
}

//  删除资产
function handleRemove(row, index) {
  proxy.$modal.confirm('是否确认移除该资产？').then(() => {
    const newData = [...props.modelValue];
    newData.splice(index, 1);
    emit("update:modelValue", newData);
    proxy.$modal.msgSuccess("移除成功");
  }).catch(() => {
  });
}

// ==================== 资产选择器相关方法 ====================
function openAssetDrawer() {
  assetDrawerVisible.value = true;
  loadSelectableAssets();
}

function loadSelectableAssets() {
  assetLoading.value = true;
  listAssets(assetQueryParams)
      .then((res) => {
        selectableAssetsList.value = res.rows || [];
        assetSelectTotal.value = res.total || 0;
      })
      .finally(() => {
        assetLoading.value = false;
      });
}

function handleQuery() {
  assetQueryParams.pageNum = 1;
  loadSelectableAssets();
}

function resetQuery() {
  Object.assign(assetQueryParams, {
    pageNum: 1,
    assetCode: null,
    assetName: null,
    ...props.assetSelectorFilter,
  });
  loadSelectableAssets();
}

function handleAssetPageChange(page) {
  assetQueryParams.pageNum = page;
  loadSelectableAssets();
}

function handleAssetLimitChange(limit) {
  assetQueryParams.pageSize = limit;
  assetQueryParams.pageNum = 1;
  loadSelectableAssets();
}

function handleSelectableAssetChange(selection) {
  ids.value = selection.map((item) => item.id);
}

function handleDrawerClose() {
  ids.value = [];
  // 清空表格选中状态
  selectorTableRef.value?.clearSelection();
}

async function confirmAssetSelect() {
  if (ids.value.length === 0) {
    proxy.$modal.msgWarning("请至少选择一个资产");
    return;
  }
  try {
    const res = await getAssetsByIds(ids.value);
    if (res.code === 200 && res.data) {
      const existingIds = new Set(props.modelValue.map((a) => a.id));
      let addedCount = 0;
      const newAssets = [...props.modelValue];

      res.data.forEach((asset) => {
        if (!existingIds.has(asset.id)) {
          newAssets.push(asset);
          addedCount++;
        }
      });

      if (addedCount > 0) {
        emit("update:modelValue", newAssets);
        proxy.$modal.msgSuccess(`成功添加 ${addedCount} 个资产`);
        assetDrawerVisible.value = false;
        // 清空选中
        ids.value = [];
      } else {
        proxy.$modal.msgWarning("所有选中的资产都已存在");
      }
    }
  } catch {
    proxy.$modal.msgError("获取资产信息失败");
  }
}

//  清空选中
function clearSelection() {
  tableRef.value?.clearSelection();
}

//  获取选中行
function getSelectionRows() {
  return selectedRows.value;
}

//  设置搜索关键词
function setSearchKeyword(keyword) {
  localSearchKeyword.value = keyword;
}

//  重新加载选项数据
function reloadOptions() {
  loadAllOptions();
}

//  更新资产选择器过滤条件
function updateSelectorFilter(filter) {
  Object.assign(assetQueryParams, filter);
  if (assetDrawerVisible.value) {
    loadSelectableAssets();
  }
}

onMounted(() => {
  loadAllOptions();
});

defineExpose({
  tableRef,
  clearSelection,
  getSelectionRows,
  setSearchKeyword,
  reloadOptions,
  updateSelectorFilter,
  openAssetDrawer,  // 暴露打开选择器的方法
});
</script>

<style scoped lang="scss">
.asset-table-wrapper {
  .table-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 0;
    margin-bottom: 10px;

    .toolbar-left {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .toolbar-right {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .total-value-label {
      font-size: 14px;
      color: var(--el-text-color-primary);
      font-weight: bold;

      .total-value {
        font-size: 16px;
        color: var(--el-color-warning);
        margin: 0 5px;
      }
    }
  }

  .asset-selector-wrapper {
    padding: 0 20px;

    .selector-toolbar {
      margin-bottom: 16px;
    }
  }
}
</style>