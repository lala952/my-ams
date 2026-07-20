/**
 * 资产模块工具函数统一导出
 * 按业务分类的工具函数集合
 *
 * 使用方式:
 * import { formatMoney, formatFileSize, formatDuration, formatEmpty } from '@/utils/asset'
 */

// 金额格式化
export {formatMoney} from './formatMoney'

// 文件大小格式化
export {formatFileSize} from './formatFileSize'

// 时间格式化
export {formatDuration} from './formatDuration'

// 空值格式化
export {formatEmpty, formatNumber, formatText, formatTableEmpty} from './formatEmpty'
