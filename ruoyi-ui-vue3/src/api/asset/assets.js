import request from '@/utils/request'

// 查询资产台账列表
export function listAssets(query) {
    return request({
        url: '/asset/assets/list',
        method: 'get',
        params: query
    })
}

// 查询当前用户名下资产列表
export function listMyAssets(query) {
    return request({
        url: '/asset/assets/my/list',
        method: 'get',
        params: query
    })
}

// 查询资产台账详细
export function getAssets(id) {
    return request({
        url: '/asset/assets/' + id,
        method: 'get'
    })
}

// 新增资产台账
export function addAssets(data) {
    return request({
        url: '/asset/assets',
        method: 'post',
        data: data
    })
}

// 修改资产台账
export function updateAssets(data) {
    return request({
        url: '/asset/assets',
        method: 'put',
        data: data
    })
}

// 删除资产台账
export function delAssets(id) {
    return request({
        url: '/asset/assets/' + id,
        method: 'delete'
    })
}

// 根据IDs批量查询资产
export function getAssetsByIds(ids) {
    return request({
        url: '/asset/assets/ids',
        method: 'get',
        params: {ids: ids.join(',')}
    })
}

//  二维码 

// 预览二维码
export function previewQRCode(id) {
    return request({
        url: '/asset/assets/qrcode/preview/' + id,
        method: 'get',
        responseType: 'blob'
    })
}

// 下载二维码
export function downloadQRCode(id) {
    return request({
        url: '/asset/assets/qrcode/download/' + id,
        method: 'get',
        responseType: 'blob'
    })
}

//  条码 

// 预览条码
export function previewBarcode(assetCode) {
    return request({
        url: '/asset/assets/barcode/preview/' + assetCode,
        method: 'get',
        responseType: 'blob'
    })
}

// 下载条码
export function downloadBarcode(assetCode) {
    return request({
        url: '/asset/assets/barcode/download/' + assetCode,
        method: 'get',
        responseType: 'blob'
    })
}

export function exportPdf(id) {
    return request({
        url: '/asset/assets/pdf/' + id,
        method: 'get',
        responseType: 'blob'
    })
}