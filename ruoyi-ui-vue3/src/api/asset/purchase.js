import request from '@/utils/request'

// 查询资产采购列表
export function listPurchase(query) {
    return request({
        url: '/asset/purchase/list',
        method: 'get',
        params: query
    })
}

// 查询资产采购详细
export function getPurchase(id) {
    return request({
        url: '/asset/purchase/' + id,
        method: 'get'
    })
}

// 新增资产采购
export function addPurchase(data) {
    return request({
        url: '/asset/purchase',
        method: 'post',
        data: data
    })
}

// 修改资产采购
export function updatePurchase(data) {
    return request({
        url: '/asset/purchase',
        method: 'put',
        data: data
    })
}

// 删除资产采购
export function delPurchase(id) {
    return request({
        url: '/asset/purchase/' + id,
        method: 'delete'
    })
}
