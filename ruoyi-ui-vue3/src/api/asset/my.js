import request from '@/utils/request'

// 查询名下资产列表
export function listMyAssets(query) {
    return request({
        url: '/asset/assets/my/list',
        method: 'get',
        params: query
    })
}

// 查询名下资产详情
export function getMyAsset(id) {
    return request({
        url: '/asset/assets/' + id,
        method: 'get'
    })
}

// 预览二维码
export function previewQRCode(id) {
    return request({
        url: '/asset/assets/barcode/qrcode/preview/' + id,
        method: 'get',
        responseType: 'blob'
    })
}

// 下载二维码
export function downloadQRCode(id) {
    return request({
        url: '/asset/assets/barcode/qrcode/download/' + id,
        method: 'get',
        responseType: 'blob'
    })
}

// 预览一维码
export function previewCode128(assetCode) {
    return request({
        url: '/asset/assets/barcode/code128/preview/' + assetCode,
        method: 'get',
        responseType: 'blob'
    })
}

// 下载一维码
export function downloadCode128(assetCode) {
    return request({
        url: '/asset/assets/barcode/code128/download/' + assetCode,
        method: 'get',
        responseType: 'blob'
    })
}

// 获取一维码URL
export function getCode128Url(assetCode) {
    return import.meta.env.VITE_APP_BASE_API + `/asset/assets/barcode/code128/preview/${assetCode}`
}

// 批量导出一维码到PDF
export function batchExportBarCodePdf(ids) {
    return request({
        url: '/asset/assets/barcode/code128/batch/pdf',
        method: 'post',
        data: ids,
        responseType: 'blob'
    })
}

// 批量导出一维码到ZIP
export function batchExportBarCodeZip(ids) {
    return request({
        url: '/asset/assets/barcode/code128/batch/export',
        method: 'post',
        data: ids,
        responseType: 'blob'
    })
}
