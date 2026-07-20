import request from '@/utils/request'

// 查询资产入库列表
export function listStorage(query) {
    return request({
        url: '/asset/storage/list',
        method: 'get',
        params: query
    })
}

// 查询资产入库详细
export function getStorage(id) {
    return request({
        url: '/asset/storage/' + id,
        method: 'get'
    })
}

// 新增资产入库
export function addStorage(data) {
    return request({
        url: '/asset/storage',
        method: 'post',
        data: data
    })
}

// 修改资产入库
export function updateStorage(data) {
    return request({
        url: '/asset/storage',
        method: 'put',
        data: data
    })
}

// 删除资产入库
export function delStorage(ids) {
    return request({
        url: '/asset/storage/' + ids,
        method: 'delete'
    })
}

// 导出资产入库
export function exportStorage(query) {
    return request({
        url: '/asset/storage/export',
        method: 'post',
        params: query
    })
}

// 批量审批
export function batchApprove(data) {
    return request({
        url: '/asset/storage/batchApprove',
        method: 'post',
        data: data
    })
}

// 统计各状态数量
export function countByStatus() {
    return request({
        url: '/asset/storage/countByStatus',
        method: 'get'
    })
}
