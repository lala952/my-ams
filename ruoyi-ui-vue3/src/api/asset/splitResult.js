import request from '@/utils/request'

// 查询资产拆分结果列表
export function listSplitResult(query) {
    return request({
        url: '/asset/splitResult/list',
        method: 'get',
        params: query
    })
}

// 查询资产拆分结果详细
export function getSplitResult(id) {
    return request({
        url: '/asset/splitResult/' + id,
        method: 'get'
    })
}

// 新增资产拆分结果
export function addSplitResult(data) {
    return request({
        url: '/asset/splitResult',
        method: 'post',
        data: data
    })
}

// 修改资产拆分结果
export function updateSplitResult(data) {
    return request({
        url: '/asset/splitResult',
        method: 'put',
        data: data
    })
}

// 删除资产拆分结果
export function delSplitResult(id) {
    return request({
        url: '/asset/splitResult/' + id,
        method: 'delete'
    })
}
