<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form
        :model="queryParams"
        ref="queryRef"
        :inline="true"
        v-show="showSearch"
        label-width="100px"
    >
      <el-form-item label="资产ID" prop="assetId">
        <el-input
            v-model="queryParams.assetId"
            placeholder="请输入资产ID"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="附件名称" prop="attachmentName">
        <el-input
            v-model="queryParams.attachmentName"
            placeholder="请输入附件名称"
            clearable
            @keyup.enter="handleQuery"
            style="width: 180px"
        />
      </el-form-item>
      <el-form-item label="附件类型" prop="attachmentType">
        <el-select
            v-model="queryParams.attachmentType"
            placeholder="请选择附件类型"
            clearable
            style="width: 150px"
        >
          <el-option
              v-for="dict in attachment_type"
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
            v-hasPermi="['asset:attachment:add']"
        >新增
        </el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['asset:attachment:edit']"
        >修改
        </el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['asset:attachment:remove']"
        >删除
        </el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="info"
            plain
            @click="handleExport"
            v-hasPermi="['asset:attachment:export']"
        >导出
        </el-button
        >
      </el-col>
      <right-toolbar
          v-model:showSearch="showSearch"
          @queryTable="getList"
      ></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table
        v-loading="loading"
        :data="attachmentList"
        @selection-change="handleSelectionChange"
        border
    >
      <el-table-column
          type="selection"
          width="55"
          align="center"
          fixed="left"
      />
      <el-table-column
          label="序号"
          type="index"
          width="55"
          align="center"
          fixed="left"
      />
      <el-table-column
          label="附件ID"
          prop="id"
          sortable
          show-overflow-tooltip
          min-width="80"
          align="center"

      />
      <el-table-column
          label="资产名称"
          prop="assetName"
          sortable
          show-overflow-tooltip


      />
      <el-table-column
          label="资产编码"
          prop="assetCode"
          sortable
          show-overflow-tooltip

          align="center"

      >
        <template #default="scope">
          <a class="link-type" @click="handleView(scope.row)">{{
              scope.row.assetCode || "-"
            }}</a>
        </template>
      </el-table-column>
      <el-table-column
          label="附件名称"
          prop="attachmentName"
          sortable
          show-overflow-tooltip
          min-width="200"


      />
      <el-table-column
          label="附件类型"
          prop="attachmentType"
          sortable
          width="100"
          align="center"
      >
        <template #default="scope">
          <dict-tag
              :options="attachment_type"
              :value="scope.row.attachmentType"
          />
        </template>
      </el-table-column>
      <el-table-column
          label="文件大小"
          prop="fileSize"
          sortable
          width="100"
          align="right"

      />
      <el-table-column
          label="上传人"
          prop="uploadPersonName"
          sortable
          width="100"


      />
      <el-table-column
          label="上传日期"
          prop="uploadDate"
          sortable

          align="center"

      >
        <template #default="scope">
          <span>{{
              parseTime(scope.row.uploadDate, "{y}-{m}-{d}") || "-"
            }}</span>
        </template>
      </el-table-column>
      <el-table-column
          label="备注"
          prop="remark"
          show-overflow-tooltip

      />
      <el-table-column
          label="操作"
          width="180"
          align="center"

          fixed="right"
      >
        <template #default="scope">
          <el-button
              link
              type="primary"
              @click="handleView(scope.row)"
              v-hasPermi="['asset:attachment:query']"
          >详情
          </el-button
          >
          <el-button link type="primary" @click="downloadFile(scope.row)"
          >下载
          </el-button
          >
          <el-button
              link
              type="primary"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['asset:attachment:edit']"
          >修改
          </el-button
          >
          <el-button
              link
              type="primary"
              @click="handleDelete(scope.row)"
              v-hasPermi="['asset:attachment:remove']"
          >删除
          </el-button
          >
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

    <!-- 添加或修改资产附件对话框 -->
    <el-dialog :title="title" v-model="open" width="800px" append-to-body>
      <el-form
          ref="attachmentRef"
          :model="form"
          :rules="rules"
          label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择资产" prop="assetId">
              <el-select
                  v-model="form.assetId"
                  placeholder="请选择资产"
                  filterable
                  clearable
                  style="width: 100%"
              >
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
            <el-form-item label="附件名称" prop="attachmentName">
              <el-input
                  v-model="form.attachmentName"
                  placeholder="请输入附件名称"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="附件类型" prop="attachmentType">
              <el-select
                  v-model="form.attachmentType"
                  placeholder="请选择附件类型"
                  style="width: 100%"
              >
                <el-option
                    v-for="dict in attachment_type"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上传文件" prop="file">
              <el-upload
                  ref="uploadRef"
                  action="#"
                  :auto-upload="false"
                  :on-change="handleFileChange"
                  :limit="1"
                  :file-list="fileList"
              >
                <el-button type="primary">选择文件</el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    支持上传图片、PDF、Word、Excel等格式，大小不超过10MB
                  </div>
                </template>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input
                  v-model="form.remark"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入内容"
              />
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

    <!-- 资产附件详情抽屉 -->
    <el-drawer
        v-model="drawerVisible"
        title="资产附件详情"
        size="50%"
        direction="rtl"
    >
      <div class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="附件ID">{{
              viewData.id
            }}
          </el-descriptions-item>
          <el-descriptions-item label="资产名称">{{
              viewData.assetName || "-"
            }}
          </el-descriptions-item>
          <el-descriptions-item label="资产编码">{{
              viewData.assetCode || "-"
            }}
          </el-descriptions-item>
          <el-descriptions-item label="附件名称">{{
              viewData.attachmentName || "-"
            }}
          </el-descriptions-item>
          <el-descriptions-item label="附件类型">
            <dict-tag
                :options="attachment_type"
                :value="viewData.attachmentType"
            />
          </el-descriptions-item>
          <el-descriptions-item label="文件大小">{{
              formatFileSize(viewData.fileSize)
            }}
          </el-descriptions-item>
          <el-descriptions-item label="上传人">{{
              viewData.uploadPersonName || "-"
            }}
          </el-descriptions-item>
          <el-descriptions-item label="上传日期">{{
              parseTime(viewData.uploadDate, "{y}-{m}-{d}") || "-"
            }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{
              viewData.remark || "-"
            }}
          </el-descriptions-item>
        </el-descriptions>
        <div class="file-preview" v-if="viewData.filePath">
          <el-divider>文件预览</el-divider>
          <el-button type="primary" @click="downloadFile(viewData)"
          >下载文件
          </el-button
          >
          <el-button @click="previewFile(viewData)">在线预览</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup lang="ts" name="Attachment">
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from "vue";
import type {Attachment, AttachmentQueryParams,} from "@/types/api/asset/attachment";
import {addAttachment, delAttachment, getAttachment, listAttachment, updateAttachment,} from "@/api/asset/attachment";
import {listAssets} from "@/api/asset/assets";
import {uploadFile} from "@/api/system/file";
import {parseTime} from "@/utils/ruoyi";
import {formatFileSize} from "@/utils/asset";
import {ElMessage} from "element-plus";

const {proxy} = getCurrentInstance();
const {attachment_type} = proxy.useDict("attachment_type");

const attachmentList = ref<Attachment[]>([]);
const open = ref<boolean>(false);
const loading = ref<boolean>(true);
const showSearch = ref<boolean>(true);
const ids = ref<number[]>([]);
const single = ref<boolean>(true);
const multiple = ref<boolean>(true);
const total = ref<number>(0);
const title = ref<string>("");
const drawerVisible = ref<boolean>(false);
const viewData = ref<Attachment>({} as Attachment);

// 下拉选项数据
const assetOptions = ref([]);
const fileList = ref([]);
const uploadFileObj = ref(null);

const data = reactive({
  form: {} as Attachment,
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    assetId: undefined,
    attachmentName: undefined,
    attachmentType: undefined,
  } as AttachmentQueryParams,
  rules: {
    assetId: [{required: true, message: "请选择资产", trigger: "change"}],
    attachmentName: [
      {required: true, message: "附件名称不能为空", trigger: "blur"},
    ],
  },
});

const {queryParams, form, rules} = toRefs(data);

// 加载资产列表
function loadAssetOptions() {
  listAssets({pageNum: 1, pageSize: 1000}).then((response) => {
    assetOptions.value = response.rows || [];
  });
}

// 文件选择处理
function handleFileChange(file, fileListData) {
  fileList.value = fileListData;
  uploadFileObj.value = file.raw;
}

// 下载文件
function downloadFile(attachment: Attachment) {
  if (attachment.filePath) {
    window.open(attachment.filePath, "_blank");
  }
}

// 预览文件
function previewFile(attachment: Attachment) {
  if (attachment.filePath) {
    window.open(attachment.filePath, "_blank");
  }
}

function getList() {
  loading.value = true;
  listAttachment(queryParams.value).then((response) => {
    attachmentList.value = response.rows;
    total.value = response.total;
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

function handleSelectionChange(selection: Attachment[]) {
  ids.value = selection.map((item) => item.id);
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
}

function cancel() {
  open.value = false;
  reset();
}

function reset() {
  form.value = {
    id: null,
    assetId: null,
    attachmentName: null,
    attachmentType: null,
    attachmentPath: null,
    fileSize: null,
    uploadPersonId: null,
    uploadDate: null,
    remark: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    delFlag: null,
  };
  fileList.value = [];
  uploadFileObj.value = null;
  proxy.resetForm("attachmentRef");
}

function handleView(row: Attachment) {
  viewData.value = row;
  drawerVisible.value = true;
}

function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加资产附件";
}

function handleUpdate(row: Attachment) {
  reset();
  const _id = row.id || ids.value[0];
  getAttachment(_id).then((response) => {
    form.value = response.data;
    open.value = true;
    title.value = "修改资产附件";
  });
}

async function submitForm() {
  proxy.$refs["attachmentRef"].validate(async (valid: boolean) => {
    if (valid) {
      // 如果有新上传的文件，先上传
      if (uploadFileObj.value) {
        try {
          const res = await uploadFile(uploadFileObj.value);
          if (res.code === 200) {
            form.value.attachmentPath = res.data.url;
            form.value.fileSize = uploadFileObj.value.size;
          }
        } catch (error) {
          ElMessage.error("文件上传失败");
          return;
        }
      }

      const submitData =
          form.value.id != null
              ? updateAttachment(form.value)
              : addAttachment(form.value);
      submitData.then(() => {
        proxy.$modal.msgSuccess(
            form.value.id != null ? "修改成功" : "新增成功",
        );
        open.value = false;
        getList();
      });
    }
  });
}

function handleDelete(row: Attachment) {
  const _ids = row.id || ids.value;
  proxy.$modal
      .confirm('是否确认删除资产附件编号为"' + _ids + '"的数据项？')
      .then(function () {
        return delAttachment(_ids);
      })
      .then(() => {
        getList();
        proxy.$modal.msgSuccess("删除成功");
      })
      .catch(() => {
      });
}

function handleExport() {
  proxy.download(
      "asset/attachment/export",
      {
        ...queryParams.value,
      },
      `资产附件_${new Date().getTime()}.xlsx`,
  );
}

onMounted(() => {
  getList();
  loadAssetOptions();
});
</script>
