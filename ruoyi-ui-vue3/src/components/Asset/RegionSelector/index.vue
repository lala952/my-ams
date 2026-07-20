<template>
  <el-cascader
    v-model="areaCodes"
    :options="processedRegionData"
    :props="cascaderProps"
    :placeholder="placeholder"
    :clearable="clearable"
    :filterable="filterable"
    :disabled="disabled"
    @change="handleChange"
    style="width: 100%"
    separator=""
  >
  </el-cascader>
</template>

<script setup lang="js">
import { ref, watch, computed } from 'vue'
import { regionData } from 'element-china-area-data'
import { useBuildAreaText } from '@/composables/asset/useBuildAreaText'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  placeholder: {
    type: String,
    default: '请选择 省份/地区-市-区/县'
  },
  clearable: {
    type: Boolean,
    default: true
  },
  filterable: {
    type: Boolean,
    default: true
  },
  disabled: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

// 使用 Composable
const { buildAreaText } = useBuildAreaText()

// 级联选择器配置
const cascaderProps = {
  expandTrigger: 'hover',
  label: 'label',
  value: 'value',
  children: 'children'
}

// 处理数据：跳过直辖市的"市辖区"层级
const processedRegionData = computed(() => {
  return regionData.map(province => {
    // 如果是直辖市（北京、上海、天津、重庆）
    if (province.children && province.children.length > 0) {
      const firstCity = province.children[0]
      // 如果第一级城市是"市辖区"或"县"，则跳过这一层
      if (firstCity.label === '市辖区' || firstCity.label === '县') {
        return {
          ...province,
          children: firstCity.children || []  // 直接使用区县级作为子级
        }
      }
    }
    return province
  })
})

// 内部状态：存储省市区编码数组 [110000, 110100, 110105]
const areaCodes = ref(props.modelValue)

// 监听父组件传入的值变化
watch(() => props.modelValue, (newVal) => {
  areaCodes.value = newVal
}, { deep: true })

// 当选择变化时，emit给父组件
const handleChange = (value) => {
  emit('update:modelValue', value)
  
  // 同时传递拼接好的地区文本，方便父组件使用
  const areaText = buildAreaText(value)
  emit('change', {
    codes: value,
    text: areaText
  })
}
</script>
