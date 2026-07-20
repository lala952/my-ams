/**
 * 格式化金额（千分位，保留两位小数）
 * @param {*} value 金额数值
 * @returns {string} 格式化后的金额字符串
 * @example formatMoney(1234567.89) => "1,234,567.89"
 */
export function formatMoney(value) {
    if (!value && value !== 0) return '0.00'
    const formatedMoney = Number(value).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    return formatedMoney
}
