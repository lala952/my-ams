import request from '@/utils/request'

// 查询资产配置计划列表
export function listPlan(query) {
    return request({
        url: '/asset/plan/list',
        method: 'get',
        params: query
    })
}

// 查询资产配置计划详细
export function getPlan(id) {
    return request({
        url: '/asset/plan/' + id,
        method: 'get'
    })
}

// 新增资产配置计划
export function addPlan(data) {
    return request({
        url: '/asset/plan',
        method: 'post',
        data: data
    })
}

// 修改资产配置计划
export function updatePlan(data) {
    return request({
        url: '/asset/plan',
        method: 'put',
        data: data
    })
}

// 删除资产配置计划
export function delPlan(ids) {
    return request({
        url: '/asset/plan/' + ids,
        method: 'delete'
    })
}

// 提交审批
export function submitPlan(id) {
    return request({
        url: '/asset/plan/submit/' + id,
        method: 'post'
    })
}

// 生成采购单
export function generatePurchase(planId) {
    return request({
        url: '/asset/plan/generatePurchase/' + planId,
        method: 'post'
    })
}
