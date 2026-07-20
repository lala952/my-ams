import request from '@/utils/request'

// 查询资产领用列表
export function listReceive(query) {
    return request({
        url: '/asset/receive/list',
        method: 'get',
        params: query
    })
}

// 查询资产领用详细
export function getReceive(id) {
    return request({
        url: '/asset/receive/' + id,
        method: 'get'
    })
}

// 新增资产领用
export function addReceive(data) {
    return request({
        url: '/asset/receive',
        method: 'post',
        data: data
    })
}

// 修改资产领用
export function updateReceive(data) {
    return request({
        url: '/asset/receive',
        method: 'put',
        data: data
    })
}

// 删除资产领用
export function delReceive(id) {
    return request({
        url: '/asset/receive/' + id,
        method: 'delete'
    })
}

// 批量审批
export function batchApprove(data) {
    return request({
        url: '/asset/receive/batchApprove',
        method: 'post',
        data: data
    })
}

// 统计各状态数量
export function countByStatus() {
    return request({
        url: '/asset/receive/countByStatus',
        method: 'get'
    })
}
