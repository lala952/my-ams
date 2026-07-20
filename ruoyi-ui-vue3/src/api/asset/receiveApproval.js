import request from '@/utils/request'

// 查询资产领用列表
export function listReceive(query) {
    return request({
        url: '/asset/receive/list',
        method: 'get',
        params: query
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
