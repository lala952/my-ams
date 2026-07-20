<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form
      :model="queryParams"
      ref="queryRef"
      :inline="true"
      v-show="showSearch"
    >
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
        <el-select
          v-model="queryParams.assetType"
          placeholder="请选择"
          clearable
          style="width: 180px"
        >
          <el-option
            v-for="dict in asset_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="资产状态" prop="assetStatus">
        <el-select
          v-model="queryParams.assetStatus"
          placeholder="请选择"
          clearable
          style="width: 180px"
        >
          <el-option
            v-for="dict in asset_status"
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
          v-hasPermi="['asset:assets:add']"
        >
          新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['asset:assets:edit']"
          >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['asset:assets:remove']"
          >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          @click="handleExport"
          v-hasPermi="['asset:assets:export']"
          >导出
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          @click="handleExportBarcodes"
          v-hasPermi="['asset:assets:export']"
          >打印资产条码
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          @click="handleViewLocations"
          v-hasPermi="['asset:assets:query']"
          >资产地图
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList" />
    </el-row>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="assetsList"
      @selection-change="handleSelectionChange"
      border
    >
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="60" align="center" />
      <el-table-column
        label="资产编码"
        prop="assetCode"
        sortable
        show-overflow-tooltip
      >
        <template #default="scope">
          <a
            class="link-type"
            style="cursor: pointer"
            @click="handleView(scope.row)"
            >{{ scope.row.assetCode }}</a
          >
        </template>
      </el-table-column>
      <el-table-column
        label="资产名称"
        prop="assetName"
        sortable
        show-overflow-tooltip
      />
      <el-table-column
        label="资产分类"
        prop="categoryId"
        sortable
        show-overflow-tooltip
      >
        <template #default="scope"
          >{{ getCategoryName(scope.row.categoryId) }}
        </template>
      </el-table-column>
      <el-table-column
        label="资产状态"
        prop="assetStatus"
        sortable
        show-overflow-tooltip
      >
        <template #default="scope">
          <dict-tag :options="asset_status" :value="scope.row.assetStatus" />
        </template>
      </el-table-column>
      <el-table-column
        label="使用人"
        prop="userId"
        sortable
        show-overflow-tooltip
      >
        <template #default="scope"
          >{{ getUserName(scope.row.userId) }}
        </template>
      </el-table-column>
      <el-table-column
        label="使用部门"
        prop="deptId"
        sortable
        show-overflow-tooltip
      >
        <template #default="scope"
          >{{ getDeptName(scope.row.deptId) }}
        </template>
      </el-table-column>
      <el-table-column
        label="存放位置"
        prop="location"
        sortable
        show-overflow-tooltip
      />
      <el-table-column
        label="原值"
        prop="originalValue"
        align="right"
        sortable
        show-overflow-tooltip
      >
        <template #default="scope"
          >{{ formatMoney(scope.row.originalValue) }} 元
        </template>
      </el-table-column>
      <el-table-column
        label="净值"
        prop="netValue"
        align="right"
        sortable
        show-overflow-tooltip
      >
        <template #default="scope"
          >{{ formatMoney(scope.row.netValue) }} 元
        </template>
      </el-table-column>
      <el-table-column align="center" label="操作" min-width="180px">
        <template #default="scope">
          <el-button-group>
            <el-button
              link
              type="primary"
              @click="handleView(scope.row)"
              v-hasPermi="['asset:assets:query']"
              >详情
            </el-button>
            <el-button
              link
              type="primary"
              @click="handleViewLocation(scope.row)"
              v-hasPermi="['asset:assets:query']"
              >位置
            </el-button>
            <el-button
              link
              type="primary"
              @click="handleQRCode(scope.row)"
              v-hasPermi="['asset:assets:query']"
              >二维码
            </el-button>
            <el-button
              link
              type="primary"
              @click="handleBarcode(scope.row)"
              v-hasPermi="['asset:assets:query']"
              >条码
            </el-button>
            <el-button
              link
              type="primary"
              @click="handleExportPdf(scope.row)"
              v-hasPermi="['asset:assets:export']"
            >
              打印
            </el-button>
            <el-button
              link
              type="primary"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['asset:assets:edit']"
              >修改
            </el-button>
            <el-button
              link
              type="primary"
              @click="handleDelete(scope.row)"
              v-hasPermi="['asset:assets:remove']"
              >删除
            </el-button>
          </el-button-group>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/修改弹窗 -->
    <el-drawer size="85%" :title="title" v-model="open" append-to-body>
      <el-form ref="assetsRef" :model="form" :rules="rules" label-width="120px">
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item label="资产编码" prop="assetCode">
              <el-input disabled v-model="form.assetCode" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="资产名称" prop="assetName">
              <el-input
                v-model="form.assetName"
                placeholder="请输入资产名称"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="资产分类" prop="categoryId">
              <el-select
                v-model="form.categoryId"
                placeholder="请选择"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="cat in categoryOptions"
                  :key="cat.id"
                  :label="cat.categoryName"
                  :value="cat.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="资产类型" prop="assetType">
              <el-select
                v-model="form.assetType"
                placeholder="请选择"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="dict in asset_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="资产来源" prop="assetSource">
              <el-select
                v-model="form.assetSource"
                placeholder="请选择"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="dict in asset_source"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="资产状态" prop="assetStatus">
              <el-select
                v-model="form.assetStatus"
                placeholder="请选择"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="dict in asset_status"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="数量" prop="quantity">
              <el-input-number
                v-model="form.quantity"
                :min="1"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位" prop="unit">
              <el-select
                v-model="form.unit"
                placeholder="请选择"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="dict in measure_unit"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">规格参数</el-divider>
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item label="规格型号" prop="model">
              <el-input
                v-model="form.model"
                placeholder="请输入规格型号"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="品牌" prop="brand">
              <el-input
                v-model="form.brand"
                placeholder="请输入品牌"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="序列号" prop="serialNumber">
              <el-input
                v-model="form.serialNumber"
                placeholder="请输入序列号"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="条码" prop="barcode">
              <el-input
                v-model="form.barcode"
                placeholder="请输入条码"
                clearable
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="RFID标签" prop="rfidTag">
              <el-input
                v-model="form.rfidTag"
                placeholder="请输入RFID标签"
                clearable
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">存放位置</el-divider>
        <el-row :gutter="10">
          <el-col :span="24">
            <el-form-item label="存放位置">
              <el-button text plain type="info" @click="handleUpdateLocation(form)"
                >点击修改资产存放位置</el-button
              >
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">财务信息</el-divider>
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item label="原值(元)" prop="originalValue">
              <el-input-number
                v-model="form.originalValue"
                :precision="2"
                :min="0"
                style="width: 100%"
                @change="calculateNetValue"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="采购价格(元)" prop="purchasePrice">
              <el-input-number
                v-model="form.purchasePrice"
                :precision="2"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="残值(元)" prop="salvageValue">
              <el-input-number
                v-model="form.salvageValue"
                :precision="2"
                :min="0"
                style="width: 100%"
                @change="calculateNetValue"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="累计折旧(元)" prop="accumulatedDepreciation">
              <el-input-number
                v-model="form.accumulatedDepreciation"
                :precision="2"
                style="width: 100%"
                @change="calculateNetValue"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="净值(元)" prop="netValue">
              <el-input-number
                v-model="form.netValue"
                :precision="2"
                style="width: 100%"
                disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">折旧信息</el-divider>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item label="折旧方法" prop="depreciationMethod">
              <el-select
                v-model="form.depreciationMethod"
                placeholder="请选择"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="dict in depreciation_method"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="折旧率(%)" prop="depreciationRate">
              <el-input
                v-model="form.depreciationRate"
                disabled
                placeholder="系统自动计算"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="开始折旧日期" prop="depreciationStartDate">
              <el-date-picker
                v-model="form.depreciationStartDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="购置日期" prop="purchaseDate">
              <el-date-picker
                v-model="form.purchaseDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择购置日期"
                style="width: 100%"
                @change="setDefaultDepreciationStartDate"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">供应商信息</el-divider>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierId">
              <el-select
                v-model="form.supplierId"
                placeholder="请选择供应商"
                filterable
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="sup in supplierOptions"
                  :key="sup.id"
                  :label="sup.supplierName"
                  :value="sup.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">使用信息</el-divider>
        <el-row :gutter="10">
          <el-col :span="9">
            <el-form-item label="使用人" prop="userId">
              <el-select
                v-model="form.userId"
                placeholder="请选择"
                filterable
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="user in userOptions"
                  :key="user.userId"
                  :label="user.nickName"
                  :value="user.userId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="9">
            <el-form-item label="使用部门" prop="deptId">
              <el-tree-select
                v-model="form.deptId"
                :data="deptOptions"
                :props="{ value: 'deptId', label: 'deptName' }"
                placeholder="请选择使用部门"
                check-strictly
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="开始使用日期" prop="startUseDate">
              <el-date-picker
                v-model="form.startUseDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">时间节点</el-divider>
        <el-row :gutter="10">
          <el-col :span="6">
            <el-form-item label="生产日期" prop="manufactureDate">
              <el-date-picker
                v-model="form.manufactureDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="保修截止日期" prop="warrantyExpireDate">
              <el-date-picker
                v-model="form.warrantyExpireDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="报废日期" prop="scrapDate">
              <el-date-picker
                v-model="form.scrapDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">其他</el-divider>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="form.remark"
                type="textarea"
                :rows="1"
                placeholder="请输入备注"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="资产主图" prop="mainImage">
              <image-upload v-model="form.mainImage" :limit="1" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="cancel">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-drawer>

    <!-- 详情抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      append-to-body
      size="85%"
      title="资产详情"
    >
      <h4 class="section-title">基本信息</h4>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="资产编码"
          ><span class="link-type">{{
            viewData.assetCode
          }}</span></el-descriptions-item
        >
        <el-descriptions-item label="资产名称" :span="2">{{
          viewData.assetName || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="资产分类">{{
          getCategoryName(viewData.categoryId)
        }}</el-descriptions-item>
        <el-descriptions-item label="资产类型"
          ><dict-tag :options="asset_type" :value="viewData.assetType"
        /></el-descriptions-item>
        <el-descriptions-item label="资产来源"
          ><dict-tag :options="asset_source" :value="viewData.assetSource"
        /></el-descriptions-item>
        <el-descriptions-item label="规格型号">{{
          viewData.model || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="品牌">{{
          viewData.brand || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="序列号">{{
          viewData.serialNumber || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="存放位置" :span="3">{{
          viewData.location || "-"
        }}</el-descriptions-item>
      </el-descriptions>

      <h4 class="section-title">财务信息</h4>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="原值">{{
          formatMoney(viewData.originalValue)
        }}</el-descriptions-item>
        <el-descriptions-item label="采购价格">{{
          formatMoney(viewData.purchasePrice)
        }}</el-descriptions-item>
        <el-descriptions-item label="残值">{{
          formatMoney(viewData.salvageValue)
        }}</el-descriptions-item>
        <el-descriptions-item label="累计折旧">{{
          formatMoney(viewData.accumulatedDepreciation)
        }}</el-descriptions-item>
        <el-descriptions-item label="净值">{{
          formatMoney(viewData.netValue)
        }}</el-descriptions-item>
        <el-descriptions-item label="净值率"
          >{{ netValueRate }}%</el-descriptions-item
        >
        <el-descriptions-item label="折旧方法"
          ><dict-tag
            :options="depreciation_method"
            :value="viewData.depreciationMethod"
        /></el-descriptions-item>
        <el-descriptions-item label="折旧率(%)">{{
          viewData.depreciationRate ? viewData.depreciationRate : "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="购置日期">{{
          parseTime(viewData.purchaseDate, "{y}-{m}-{d}") || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="开始折旧日期">{{
          parseTime(viewData.depreciationStartDate, "{y}-{m}-{d}") || "-"
        }}</el-descriptions-item>
      </el-descriptions>

      <h4 class="section-title">使用信息</h4>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="数量">{{
          viewData.quantity || 0
        }}</el-descriptions-item>
        <el-descriptions-item label="单位"
          ><dict-tag :options="measure_unit" :value="viewData.unit"
        /></el-descriptions-item>
        <el-descriptions-item label="使用人">{{
          getUserName(viewData.userId)
        }}</el-descriptions-item>
        <el-descriptions-item label="使用部门">{{
          getDeptName(viewData.deptId)
        }}</el-descriptions-item>
        <el-descriptions-item label="开始使用日期">{{
          parseTime(viewData.startUseDate, "{y}-{m}-{d}") || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="生产日期">{{
          parseTime(viewData.manufactureDate, "{y}-{m}-{d}") || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="保修截止日期">{{
          parseTime(viewData.warrantyExpireDate, "{y}-{m}-{d}") || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="报废日期">{{
          parseTime(viewData.scrapDate, "{y}-{m}-{d}") || "-"
        }}</el-descriptions-item>
      </el-descriptions>

      <h4 class="section-title">供应商</h4>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="供应商">{{
          getSupplierName(viewData.supplierId)
        }}</el-descriptions-item>
        <el-descriptions-item label="条码">{{
          viewData.barcode || "-"
        }}</el-descriptions-item>
        <el-descriptions-item label="RFID标签">{{
          viewData.rfidTag || "-"
        }}</el-descriptions-item>
      </el-descriptions>

      <h4 class="section-title">备注</h4>
      <el-descriptions border>
        <el-descriptions-item label="备注">{{
          viewData.remark || "-"
        }}</el-descriptions-item>
      </el-descriptions>

      <h4 class="section-title" v-if="viewData.mainImage">资产图片</h4>
      <el-descriptions :column="1" border v-if="viewData.mainImage">
        <el-descriptions-item label="主图">
          <image-preview :src="viewData.mainImage" :width="100" :height="100" />
        </el-descriptions-item>
      </el-descriptions>
    </el-drawer>
    <!-- 二维码弹窗 -->
    <el-dialog
      v-model="qrcodeVisible"
      title="资产二维码"
      width="450px"
      append-to-body
      @closed="closeQRCodeDialog"
    >
      <el-card shadow="never" body-style="text-align: center; padding: 20px">
        <h3>{{ currentAsset.assetName || "-" }}</h3>
        <p>
          资产编码：<el-link type="primary" @click="handleView(currentAsset)">{{
            currentAsset.assetCode
          }}</el-link>
        </p>
        <div
          style="
            margin: 20px 0;
            display: flex;
            justify-content: center;
            min-height: 200px;
            align-items: center;
          "
        >
          <img
            v-if="qrcodeImageUrl"
            :src="qrcodeImageUrl"
            style="width: 200px; height: 200px"
          />
          <el-empty v-else :image-size="40" description="暂无二维码" />
        </div>
        <p style="color: var(--el-color-primary); margin: 10px 0 5px 0">
          手机扫码查看资产信息
        </p>
        <p style="font-size: 11px; color: #909399; margin-top: 5px">
          扫码后将跳转到资产详情页面
        </p>
      </el-card>
      <template #footer>
        <el-button @click="qrcodeVisible = false">关闭</el-button>
        <el-button @click="downloadQRCodeImage">下载</el-button>
        <el-button type="primary" @click="printQRCode">打印</el-button>
      </template>
    </el-dialog>

    <!-- 条码弹窗 -->
    <el-dialog
      v-model="barcodeVisible"
      title="资产条码"
      width="500px"
      append-to-body
      @closed="closeBarcodeDialog"
    >
      <el-card shadow="never" body-style="text-align: center; padding: 20px">
        <h3>{{ currentAsset.assetName }}</h3>
        <p>
          资产编码：<el-link type="primary" @click="handleView(currentAsset)">{{
            currentAsset.assetCode
          }}</el-link>
        </p>
        <div
          style="
            margin: 20px 0;
            display: flex;
            justify-content: center;
            min-height: 100px;
            align-items: center;
          "
        >
          <img
            v-if="barcodeImageUrl"
            :src="barcodeImageUrl"
            style="height: 80px"
          />
          <el-empty v-else :image-size="40" description="暂无条码" />
        </div>
      </el-card>
      <template #footer>
        <el-button @click="barcodeVisible = false">关闭</el-button>
        <el-button @click="downloadBarcodeImage">下载</el-button>
        <el-button type="primary" @click="printBarcode">打印</el-button>
      </template>
    </el-dialog>

    <!-- 天地图 -->
    <el-dialog
      :title="title"
      v-model="showMap"
      width="80%"
      append-to-body
    >
      <Tianditu
        v-model="tiandiAssets"
        :isView="isView"
        @submit="handleSubmitLocation"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts" name="Assets">
import {
  computed,
  getCurrentInstance,
  onMounted,
  onUnmounted,
  reactive,
  ref,
  toRefs,
  watch,
} from "vue";
import { useRoute } from "vue-router";
import { formatMoney } from "@/utils/asset";
import { useAssetOptions } from "@/composables/asset/useAssetOptions";
import Tianditu from "@/components/Asset/Tianditu";
import {
  addAssets,
  delAssets,
  downloadBarcode,
  downloadQRCode,
  exportPdf,
  getAssets,
  listAssets,
  previewBarcode,
  previewQRCode,
  updateAssets,
} from "@/api/asset/assets";
import ImageUpload from "../../../components/ImageUpload/index.vue";

const route = useRoute();
const { proxy } = getCurrentInstance();
const {
  depreciation_method,
  asset_type,
  asset_source,
  measure_unit,
  asset_status,
} = proxy.useDict(
  "depreciation_method",
  "asset_type",
  "asset_source",
  "measure_unit",
  "asset_status",
);

const assetsList = ref<[]>([]);
const open = ref<boolean>(false);
const loading = ref<boolean>(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref<string>("");
const drawerVisible = ref(false);
const viewData = ref({});

const qrcodeVisible = ref(false);
const barcodeVisible = ref(false);
const currentAsset = ref({});
const qrcodeImageUrl = ref("");
const barcodeImageUrl = ref("");

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
    userId: undefined,
  },
  rules: {
    assetName: [
      { required: true, message: "资产名称不能为空", trigger: "blur" },
    ],
    assetType: [
      { required: true, message: "资产类型不能为空", trigger: "change" },
    ],
    assetSource: [
      { required: true, message: "资产来源不能为空", trigger: "change" },
    ],
    quantity: [
      { required: true, message: "数量不能为空", trigger: "blur" },
      { type: "number", min: 1, message: "数量必须大于等于1", trigger: "blur" },
    ],
    unit: [{ required: true, message: "单位不能为空", trigger: "change" }],
    originalValue: [
      { required: true, message: "原值不能为空", trigger: "blur" },
      { type: "number", min: 0, message: "原值不能为负数", trigger: "blur" },
    ],
    purchasePrice: [
      { required: true, message: "采购价格不能为空", trigger: "blur" },
      {
        type: "number",
        min: 0,
        message: "采购价格不能为负数",
        trigger: "blur",
      },
    ],
    supplierId: [
      { required: true, message: "供应商不能为空", trigger: "change" },
    ],
    purchaseDate: [
      { required: true, message: "购置日期不能为空", trigger: "change" },
    ],
    assetStatus: [
      { required: true, message: "资产状态不能为空", trigger: "change" },
    ],
    longitude: [
      {
        required: true,
        message: "请选择资产存放经",
        trigger: "blur",
      },
    ],
    latitude: [
      {
        required: true,
        message: "请选择资产存放纬度",
        trigger: "blur",
      },
    ],
  },
});
const { queryParams, form, rules } = toRefs(data);

const {
  userOptions,
  deptOptions,
  categoryOptions,
  supplierOptions,
  getUserName,
  getDeptName,
  getCategoryName,
  getSupplierName,
  loadAll: loadOptions,
} = useAssetOptions();

const calculateNetValue = () => {
  const original = Number(form.value.originalValue) || 0;
  const accumulated = Number(form.value.accumulatedDepreciation) || 0;
  form.value.netValue = (original - accumulated).toFixed(2);
};

const setDefaultDepreciationStartDate = () => {
  if (form.value.purchaseDate && !form.value.depreciationStartDate) {
    const date = new Date(form.value.purchaseDate);
    date.setMonth(date.getMonth() + 1);
    date.setDate(1);
    form.value.depreciationStartDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")}`;
  }
};

const netValueRate = computed(() => {
  const original = Number(viewData.value.originalValue) || 0;
  const net = Number(viewData.value.netValue) || 0;
  if (original === 0) return 0;
  return Math.round((net / original) * 100);
});

function getList() {
  loading.value = true;
  listAssets(queryParams.value).then((res) => {
    assetsList.value = res.rows;
    total.value = res.total;
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

function handleSelectionChange(selection: any[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

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
  proxy.resetForm("assetsRef");
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加资产台账";
  setDefaultDepreciationStartDate();
}

function handleView(row: any) {
  viewData.value = row;
  drawerVisible.value = true;
}

function handleUpdate(row: any) {
  reset();
  getAssets(row.id || ids.value[0]).then((res) => {
    form.value = res.data;
    calculateNetValue();
    open.value = true;
    title.value = "修改资产台账";
  });
}

function submitForm() {
  proxy.$refs["assetsRef"].validate((valid: boolean) => {
    if (valid) {
      if (form.value.id != null) {
        updateAssets(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addAssets(form.value).then(() => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

function cancel() {
  open.value = false;
  reset();
}

function handleDelete(row: any) {
  const _ids = row.id || ids.value;
  proxy.$modal
    .confirm('是否确认删除资产台账编号为"' + _ids + '"的数据项？')
    .then(() => delAssets(_ids))
    .then(() => {
      getList();
      proxy.$modal.msgSuccess("删除成功");
    })
    .catch(() => {});
}

function handleExport() {
  proxy.download(
    "asset/assets/export",
    { ...queryParams.value },
    `资产台账_${new Date().getTime()}.xlsx`,
  );
}

function handleExportBarcodes() {
  proxy.download(
    "asset/assets/export/barcodes/pdf",
    { ...queryParams.value },
    `资产台账条码_${new Date().getTime()}.pdf`,
  );
}

function handleExportPdf(row: any) {
  exportPdf(row.id)
    .then((blob) => {
      const url = window.URL.createObjectURL(
        new Blob([blob], { type: "application/pdf" }),
      );
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `资产台账_${row.assetCode}.pdf`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      proxy.$modal.msgSuccess("PDF导出成功");
    })
    .catch((reason) => {
      proxy.$modal.msgError(reason);
    });
}

function handleQRCode(row: any) {
  currentAsset.value = row;
  qrcodeVisible.value = true;
  qrcodeImageUrl.value = "";
  previewQRCode(row.id)
    .then((blob) => {
      if (qrcodeImageUrl.value) URL.revokeObjectURL(qrcodeImageUrl.value);
      qrcodeImageUrl.value = URL.createObjectURL(blob);
    })
    .catch(() => {
      proxy.$modal.msgError("二维码生成失败");
      qrcodeVisible.value = false;
    });
}

function downloadQRCodeImage() {
  downloadQRCode(currentAsset.value.id)
    .then((blob) => {
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `${currentAsset.value.assetCode}.png`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
    })
    .catch(() => proxy.$modal.msgError("二维码下载失败"));
}

function closeQRCodeDialog() {
  if (qrcodeImageUrl.value) URL.revokeObjectURL(qrcodeImageUrl.value);
  qrcodeImageUrl.value = "";
}

function printQRCode() {
  const printContent = document.getElementById("qrcode-print-area");
  if (!printContent) return;
  const printWindow = window.open("", "_blank");
  printWindow.document.write(
    `<html><head><title>资产二维码</title><style>body{margin:0;padding:20px;display:flex;justify-content:center;align-items:center;min-height:100vh}.qrcode-container{text-align:center;padding:20px}.qrcode-title h3{margin:0 0 10px;font-size:18px;color:#303133}.qrcode-title p{margin:0 0 20px;font-size:14px;color:#909399}.qrcode-image{margin:20px 0;display:flex;justify-content:center}.qrcode-info p{margin:0;font-size:12px;color:#c0c4cc}</style></head><body>${printContent.innerHTML}</body></html>`,
  );
  printWindow.document.close();
  printWindow.focus();
  setTimeout(() => {
    printWindow.print();
    printWindow.close();
  }, 250);
}

function handleBarcode(row: any) {
  currentAsset.value = row;
  barcodeVisible.value = true;
  barcodeImageUrl.value = "";
  previewBarcode(row.assetCode)
    .then((blob) => {
      if (barcodeImageUrl.value) URL.revokeObjectURL(barcodeImageUrl.value);
      barcodeImageUrl.value = URL.createObjectURL(blob);
    })
    .catch(() => {
      proxy.$modal.msgError("条码生成失败");
      barcodeVisible.value = false;
    });
}

function downloadBarcodeImage() {
  downloadBarcode(currentAsset.value.assetCode)
    .then((blob) => {
      const url = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.setAttribute("download", `${currentAsset.value.assetCode}.png`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
    })
    .catch(() => proxy.$modal.msgError("条码下载失败"));
}

function closeBarcodeDialog() {
  if (barcodeImageUrl.value) URL.revokeObjectURL(barcodeImageUrl.value);
  barcodeImageUrl.value = "";
}

function printBarcode() {
  const printContent = document.getElementById("barcode-print-area");
  if (!printContent) return;
  const printWindow = window.open("", "_blank");
  printWindow.document.write(
    `<html><head><title>资产条码</title><style>body{margin:0;padding:20px;display:flex;justify-content:center;align-items:center;min-height:100vh}.barcode-container{text-align:center;padding:20px}.barcode-title h3{margin:0 0 10px;font-size:18px;color:#303133}.barcode-title p{margin:0 0 20px;font-size:14px;color:#909399}.barcode-image{margin:20px 0;display:flex;justify-content:center}.barcode-info p{margin:5px 0;font-size:12px;color:#606266}</style></head><body>${printContent.innerHTML}</body></html>`,
  );
  printWindow.document.close();
  printWindow.focus();
  setTimeout(() => {
    printWindow.print();
    printWindow.close();
  }, 250);
}

/** 天地图 */
const showMap = ref(false);
const tiandiAssets = ref({});
const isView = ref<Boolean>(false);
function handleViewLocation(row: any) {
  tiandiAssets.value = row;
  showMap.value = true;
  isView.value = true;
  title.value = `资产地图(${row.assetName})`;
}
function handleViewLocations() {
  tiandiAssets.value = assetsList.value;
  showMap.value = true;
  isView.value = true;
  title.value = "资产地图";
}
function handleUpdateLocation(row: any) {
  tiandiAssets.value = row;
  showMap.value = true;
  isView.value = false;
  title.value = `资产地图(${row.assetName}) `;
}

function handleSubmitLocation(data: any) {
  showMap.value = false;

  if (!Array.isArray(data)) {
    form.value.location = data.location;
    form.value.longitude = data.longitude;
    form.value.latitude = data.latitude;
  }
  proxy.$modal.msgSuccess("成功修改存放位置");
}

watch(
  () => form.value.originalValue,
  () => calculateNetValue(),
);
watch(
  () => form.value.accumulatedDepreciation,
  () => calculateNetValue(),
);
watch(
  () => form.value.purchaseDate,
  () => {
    if (form.value.purchaseDate && !form.value.depreciationStartDate)
      setDefaultDepreciationStartDate();
  },
);

onMounted(async () => {
  await loadOptions();
  getList();
});
</script>

<style scoped lang="scss"></style>
