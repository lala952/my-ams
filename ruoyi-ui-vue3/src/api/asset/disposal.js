import request from '@/utils/request'

// 查询资产处置列表
export function listDisposal(query) {
    return request({
        url: '/asset/disposal/list',
        method: 'get',
        params: query
    })
}

// 批量审批
export function batchApprove(data) {
    return request({
        url: '/asset/disposal/batchApprove',
        method: 'post',
        data: data
    })
}

// 统计各状态数量
export function countByStatus() {
    return request({
        url: '/asset/disposal/countByStatus',
        method: 'get'
    })
}
