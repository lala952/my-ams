import request from '@/utils/request'

// 查询资产交回列表
export function listReturn(query) {
    return request({
        url: '/asset/return/list',
        method: 'get',
        params: query
    })
}

// 批量审批
export function batchApprove(data) {
    return request({
        url: '/asset/return/batchApprove',
        method: 'post',
        data: data
    })
}

// 统计各状态数量
export function countByStatus() {
    return request({
        url: '/asset/return/countByStatus',
        method: 'get'
    })
}
