import request from '@/utils/request'

// 查询资产维修列表
export function listMaintain(query) {
    return request({
        url: '/asset/maintain/list',
        method: 'get',
        params: query
    })
}

// 查询资产维修详细
export function getMaintain(id) {
    return request({
        url: '/asset/maintain/' + id,
        method: 'get'
    })
}

// 新增资产维修
export function addMaintain(data) {
    return request({
        url: '/asset/maintain',
        method: 'post',
        data: data
    })
}

// 修改资产维修
export function updateMaintain(data) {
    return request({
        url: '/asset/maintain',
        method: 'put',
        data: data
    })
}

// 删除资产维修
export function delMaintain(id) {
    return request({
        url: '/asset/maintain/' + id,
        method: 'delete'
    })
}

// 批量审批
export function batchApprove(data) {
    return request({
        url: '/asset/maintain/batchApprove',
        method: 'post',
        data: data
    })
}

// 统计各状态数量
export function countByStatus() {
    return request({
        url: '/asset/maintain/countByStatus',
        method: 'get'
    })
}
