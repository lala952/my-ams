/**
 * 格式化空值
 * @param {*} value - 需要格式化的值
 * @param {string} defaultValue - 默认值，默认为 '-'
 * @returns {string} 格式化后的值
 */
export function formatEmpty(value, defaultValue = '-') {
    if (value === null || value === undefined || value === '') {
        return defaultValue
    }
    return value
}

/**
 * 格式化数字空值
 * @param {*} value - 需要格式化的数字
 * @param {string} defaultValue - 默认值，默认为 '-'
 * @returns {string|number} 格式化后的值
 */
export function formatNumber(value, defaultValue = '-') {
    if (value === null || value === undefined || value === '') {
        return defaultValue
    }
    return value
}

/**
 * 格式化文本空值（用于 template 中）
 * @param {*} value - 需要格式化的值
 * @returns {string} 格式化后的值
 */
export function formatText(value) {
    return formatEmpty(value)
}

/**
 * 表格列空值格式化器（用于 el-table-column 的 formatter 属性）
 * Element Plus 的 formatter 签名: (row, column, cellValue, index.vue)
 * @param {Object} row - 行数据
 * @param {Object} column - 列配置
 * @param {*} cellValue - 单元格值
 * @param {number} index.vue - 行索引（由 Element Plus 自动传入）
 * @returns {string} 格式化后的值
 */
export function formatTableEmpty(row, column, cellValue) {
    if (cellValue === null || cellValue === undefined || cellValue === '') {
        return '-'
    }
    return cellValue
}
