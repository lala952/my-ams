import { codeToText } from 'element-china-area-data'

/**
 * 根据省市区编码数组构建地区文本
 * @param {Array} codes - 省市区编码数组，例如 [440000, 440100, 440106]
 * @returns {String} 拼接好的地区文本，例如 "广东省广州市天河区"
 */
export function buildAreaText(codes) {
  if (!codes || codes.length === 0) return ''
  
  const province = codeToText[codes[0]] || ''
  
  // 处理直辖市特殊情况（北京、上海、天津、重庆）
  // 跳过"市辖区"层级后，编码数组只有两级：[110000, 110101]
  let city = ''
  let district = ''
  
  if (codes.length === 2) {
    // 只有两级：可能是直辖市（跳过市辖区后）或普通省市
    const secondName = codeToText[codes[1]] || ''
    // 如果第二级是区县（编码6位），说明是直辖市
    if (codes[1].toString().length === 6) {
      // 直辖市：省级 + 区级
      district = secondName
    } else {
      // 普通省市：省级 + 市级
      city = secondName
    }
  } else if (codes.length === 3) {
    // 三级：普通省市区
    city = codeToText[codes[1]] || ''
    district = codeToText[codes[2]] || ''
  }
  
  return [province, city, district].filter(Boolean).join('')
}

/**
 * Composable: 提供地址文本构建功能
 * @returns {Object} 包含 buildAreaText 函数
 */
export function useBuildAreaText() {
  return {
    buildAreaText
  }
}
