import request from '@/utils/request'

// 查询资产调拨列表
export function listTransfer(query) {
    return request({
        url: '/asset/transfer/list',
        method: 'get',
        params: query
    })
}

// 查询资产调拨详细
export function getTransfer(id) {
    return request({
        url: '/asset/transfer/' + id,
        method: 'get'
    })
}

// 新增资产调拨
export function addTransfer(data) {
    return request({
        url: '/asset/transfer',
        method: 'post',
        data: data
    })
}

// 修改资产调拨
export function updateTransfer(data) {
    return request({
        url: '/asset/transfer',
        method: 'put',
        data: data
    })
}

// 删除资产调拨
export function delTransfer(id) {
    return request({
        url: '/asset/transfer/' + id,
        method: 'delete'
    })
}

// 批量审批
export function batchApprove(data) {
    return request({
        url: '/asset/transfer/batchApprove',
        method: 'post',
        data: data
    })
}

// 统计各状态数量
export function countByStatus() {
    return request({
        url: '/asset/transfer/countByStatus',
        method: 'get'
    })
}
