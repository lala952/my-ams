import request from '@/utils/request'

// 查询资产验收列表
export function listAcceptance(query) {
    return request({
        url: '/asset/acceptance/list',
        method: 'get',
        params: query
    })
}

// 查询资产验收详细
export function getAcceptance(id) {
    return request({
        url: '/asset/acceptance/' + id,
        method: 'get'
    })
}

// 新增资产验收
export function addAcceptance(data) {
    return request({
        url: '/asset/acceptance',
        method: 'post',
        data: data
    })
}

// 修改资产验收
export function updateAcceptance(data) {
    return request({
        url: '/asset/acceptance',
        method: 'put',
        data: data
    })
}

// 删除资产验收
export function delAcceptance(id) {
    return request({
        url: '/asset/acceptance/' + id,
        method: 'delete'
    })
}
