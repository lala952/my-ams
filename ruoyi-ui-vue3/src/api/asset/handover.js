import request from '@/utils/request'

// 查询资产交接列表
export function listHandover(query) {
    return request({
        url: '/asset/handover/list',
        method: 'get',
        params: query
    })
}

// 查询资产交接详细
export function getHandover(id) {
    return request({
        url: '/asset/handover/' + id,
        method: 'get'
    })
}

// 新增资产交接
export function addHandover(data) {
    return request({
        url: '/asset/handover',
        method: 'post',
        data: data
    })
}

// 修改资产交接
export function updateHandover(data) {
    return request({
        url: '/asset/handover',
        method: 'put',
        data: data
    })
}

// 删除资产交接
export function delHandover(ids) {
    return request({
        url: '/asset/handover/' + ids,
        method: 'delete'
    })
}

// 批量审批
export function batchApprove(data) {
    return request({
        url: '/asset/handover/batchApprove',
        method: 'post',
        data: data
    })
}

// 统计各状态数量
export function countByStatus() {
    return request({
        url: '/asset/handover/countByStatus',
        method: 'get'
    })
}
