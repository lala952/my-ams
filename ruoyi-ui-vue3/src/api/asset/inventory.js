import request from '@/utils/request'

// 查询资产盘点列表
export function listInventory(query) {
    return request({
        url: '/asset/inventory/list',
        method: 'get',
        params: query
    })
}

// 查询资产盘点详细
export function getInventory(id) {
    return request({
        url: '/asset/inventory/' + id,
        method: 'get'
    })
}

// 新增资产盘点
export function addInventory(data) {
    return request({
        url: '/asset/inventory',
        method: 'post',
        data: data
    })
}

// 修改资产盘点
export function updateInventory(data) {
    return request({
        url: '/asset/inventory',
        method: 'put',
        data: data
    })
}

// 删除资产盘点
export function delInventory(id) {
    return request({
        url: '/asset/inventory/' + id,
        method: 'delete'
    })
}
