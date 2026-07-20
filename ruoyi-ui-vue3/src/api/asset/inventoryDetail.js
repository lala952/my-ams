import request from '@/utils/request'

// 查询资产盘点明细列表
export function listInventoryDetail(query) {
    return request({
        url: '/asset/inventoryDetail/list',
        method: 'get',
        params: query
    })
}

// 查询资产盘点明细详细
export function getInventoryDetail(id) {
    return request({
        url: '/asset/inventoryDetail/' + id,
        method: 'get'
    })
}

// 新增资产盘点明细
export function addInventoryDetail(data) {
    return request({
        url: '/asset/inventoryDetail',
        method: 'post',
        data: data
    })
}

// 修改资产盘点明细
export function updateInventoryDetail(data) {
    return request({
        url: '/asset/inventoryDetail',
        method: 'put',
        data: data
    })
}

// 删除资产盘点明细
export function delInventoryDetail(id) {
    return request({
        url: '/asset/inventoryDetail/' + id,
        method: 'delete'
    })
}
