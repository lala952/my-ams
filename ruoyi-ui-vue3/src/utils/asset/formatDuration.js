/**
 * 格式化耗时（毫秒 → 小时/分钟/秒）
 * @param {number} millis 毫秒数
 * @returns {string} 格式化后的时间字符串
 * @example formatDuration(3661000) => "1小时1分钟"
 * @example formatDuration(125000) => "2分钟5秒"
 */
export function formatDuration(millis) {
    if (!millis) return ''
    const seconds = Math.floor(millis / 1000)
    const minutes = Math.floor(seconds / 60)
    const hours = Math.floor(minutes / 60)

    if (hours > 0) {
        return `${hours}小时${minutes % 60}分钟`
    } else if (minutes > 0) {
        return `${minutes}分钟${seconds % 60}秒`
    } else {
        return `${seconds}秒`
    }
}
