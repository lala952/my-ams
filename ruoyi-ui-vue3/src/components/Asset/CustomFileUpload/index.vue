<template>
  <el-button plain @click="handleShowDialog" style="width: 120px;height: 120px;padding:5px;"><el-icon  style="font-size: 30px;color: var(--el-border-color-dark)"><Plus/></el-icon></el-button>
  <el-dialog v-model="showDialog" title="添加附件" width="40%" append-to-body>
    <el-upload
        drag
        class="upload-demo"
        :action="uploadFileUrl"
        :before-upload="handleBeforeUpload"
        :file-list="fileList"
        :data="data"
        :limit="limit"
        :on-error="handleUploadError"
        :on-exceed="handleExceed"
        :on-success="handleUploadSuccess"
        :show-file-list="false"
        :headers="headers"
        ref="fileUpload"
        v-if="!disabled"
    >
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">
        <em>点击</em>或<em>拖动</em>文件到这里上传
      </div>
      <template #tip >
        <el-alert type="info" title="温馨提示" style="margin-top: 25px" :closable="closable">
          <ul>
            <li>单个附件大小不超过{{ fileSize }}M</li>
            <li>当前分组允许上传附件数量不超过{{limit}}个</li>
            <li>允许上传附件的文件类型如下：</li>
            <li><span v-for="item in fileType" style="color: var(--el-color-primary)">{{ item }} / </span></li >
            <li>如无当前分组相关材料，可不上传附件</li>
          </ul>
        </el-alert>
      </template>
    </el-upload>
    <template #footer>
      <el-button v-if="fileList.length" @click="showFileList = !showFileList">已上传({{ fileList.length }})</el-button>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" :loading="saveLoading" @click="handleUpload">确定</el-button>
    </template>
  </el-dialog>
  <el-drawer v-model="showFileList" title="已上传文件列表"  append-to-body>
        <transition-group ref="uploadFileList" class="upload-file-list el-upload-list el-upload-list--text"
                          name="el-fade-in-linear" tag="ul">
          <li :key="file.uid" class="el-upload-list__item ele-upload-list__item-content" v-for="(file, index) in fileList">
            <el-link :href="file.url" underline="never" target="_blank" style="vertical-align: center;">
                <span class="link-type"><el-icon style="vertical-align: text-top;margin-right: 5px;"><Folder/></el-icon> {{ getFileName(file.name) }} </span>
            </el-link>
            <div class="ele-upload-list__item-content-action">
              <el-link underline="never" @click="handleDelete(index)" type="primary" v-if="!disabled" icon="Delete"/>
            </div>
          </li>
        </transition-group>
  </el-drawer>
</template>

<script setup>
import {UploadFilled} from '@element-plus/icons-vue'
import {nextTick, onMounted, ref, watch} from 'vue'
import {getToken} from "@/utils/auth"
import Sortable from 'sortablejs'

const { proxy } = getCurrentInstance()
const emit = defineEmits(['update:modelValue'])

const showDialog = ref(false)
const showFileList = ref(false)
const closable = ref(false)
const saveLoading = ref(false)
const number = ref(0)
const uploadList = ref([])
const fileList = ref([])


const props = defineProps({
  modelValue: [String, Object, Array],
  action: {
    type: String,
    default: "/file/upload"
  },
  data: {
    type: Object,
    default: () => ({})
  },
  limit: {
    type: Number,
    default: 5
  },
  fileSize: {
    type: Number,
    default: 10
  },
  fileType: {
    type: Array,
    default: () => ["doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "pdf", "zip", "rar"]
  },
  isShowTip: {
    type: Boolean,
    default: true
  },
  disabled: {
    type: Boolean,
    default: false
  },
  drag: {
    type: Boolean,
    default: true
  }
})
const uploadFileUrl = ref(import.meta.env.VITE_APP_BASE_API + props.action)

const headers = ref({ Authorization: "Bearer " + getToken() })

const handleShowDialog = () => {
  if (props.disabled) return
  showDialog.value = true
}

const handleCancel = () => {
  showDialog.value = false
  saveLoading.value = false
}

const handleUpload = () => {
  saveLoading.value = false
  showDialog.value = false
}

watch(() => props.modelValue, val => {
  if (val) {
    let temp = 1
    const list = Array.isArray(val) ? val : props.modelValue.split(',')
    fileList.value = list.map(item => {
      if (typeof item === "string") {
        item = { name: item, url: item }
      }
      item.uid = item.uid || new Date().getTime() + temp++
      return item
    })
  } else {
    fileList.value = []
  }
}, { deep: true, immediate: true })

function handleBeforeUpload(file) {
  if (props.fileType.length) {
    const fileName = file.name.split('.')
    const fileExt = fileName[fileName.length - 1].toLowerCase()
    const isTypeOk = props.fileType.some(type => type.toLowerCase() === fileExt)
    if (!isTypeOk) {
      proxy.$modal.msgError(`文件格式不正确，请上传${props.fileType.join("/")}格式文件!`)
      return false
    }
  }

  if (file.name.includes(',')) {
    proxy.$modal.msgError('文件名不正确，不能包含英文逗号!')
    return false
  }

  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize
    if (!isLt) {
      proxy.$modal.msgError(`上传文件大小不能超过 ${props.fileSize} MB!`)
      return false
    }
  }

  saveLoading.value = true
  proxy.$modal.loading("正在上传文件，请稍候...")
  number.value++
  return true
}

function handleExceed() {
  proxy.$modal.msgError(`上传文件数量不能超过 ${props.limit} 个!`)
  setTimeout(() => {showDialog.value = false},100)
}

function handleUploadError(err) {
  saveLoading.value = false
  proxy.$modal.msgError("上传文件失败")
  proxy.$modal.closeLoading()
}

function handleUploadSuccess(res, file) {
  if (res.code === 200) {
    proxy.$modal.msgSuccess(`上传成功!`)
    uploadList.value.push({name: res.data.name, url: res.data.url})
    uploadedSuccessfully()
  } else {
    number.value--
    saveLoading.value = false
    proxy.$modal.closeLoading()
    proxy.$modal.msgError(res.msg)
    if (proxy.$refs.fileUpload) {
      proxy.$refs.fileUpload.handleRemove(file)
    }
    uploadedSuccessfully()
  }
}

function handleDelete(index) {
  fileList.value.splice(index, 1)
  emit("update:modelValue", listToString(fileList.value))
}

function uploadedSuccessfully() {
  if (number.value > 0 && uploadList.value.length === number.value) {
    fileList.value = fileList.value.filter(f => f.url !== undefined).concat(uploadList.value)
    uploadList.value = []
    number.value = 0
    saveLoading.value = false
    emit("update:modelValue", listToString(fileList.value))
    proxy.$modal.closeLoading()
  }
}

function getFileName(name) {
  if (name && name.lastIndexOf("/") > -1) {
    return name.slice(name.lastIndexOf("/") + 1)
  }
  return name
}

function listToString(list, separator) {
  let strs = ""
  separator = separator || ","
  for (let i in list) {
    if (list[i].url) {
      strs += list[i].url + separator
    }
  }
  return strs !== '' ? strs.substr(0, strs.length - 1) : ''
}

onMounted(() => {
  if (props.drag && !props.disabled) {
    nextTick(() => {
      const element = proxy.$refs.uploadFileList?.$el || proxy.$refs.uploadFileList
      if (element) {
        Sortable.create(element, {
          ghostClass: 'file-upload-darg',
          onEnd: (evt) => {
            const movedItem = fileList.value.splice(evt.oldIndex, 1)[0]
            fileList.value.splice(evt.newIndex, 0, movedItem)
            emit('update:modelValue', listToString(fileList.value))
          }
        })
      }
    })
  }
})
</script>

<style scoped lang="scss">
.file-upload-darg {
  opacity: 0.5;
  background: #c8ebfb;
}

.upload-file-list .el-upload-list__item {
  line-height: 2;
  margin-bottom: 10px;
  position: relative;
  transition: none !important;
}

.upload-file-list .ele-upload-list__item-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: inherit;
}

.ele-upload-list__item-content-action .el-link {
  margin-right: 10px;
}
</style>