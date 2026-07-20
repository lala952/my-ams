/**
 * 格式化文件大小（B → KB → MB → GB）
 * @param {*} bytes 字节数
 * @returns {string} 格式化后的文件大小字符串
 * @example formatFileSize(1024) => "1.00 KB"
 * @example formatFileSize(1048576) => "1.00 MB"
 */
export function formatFileSize(bytes) {
    if (!bytes) return '0 B'
    if (bytes < 1024) return bytes + ' B'
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
    if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
    return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
}
