import request from '@/utils/request'

// 查询资产详情列表
export function listDetail(query) {
    return request({
        url: '/asset/detail/list',
        method: 'get',
        params: query
    })
}

// 查询资产详情详细
export function getDetail(id) {
    return request({
        url: '/asset/detail/' + id,
        method: 'get'
    })
}

// 新增资产详情
export function addDetail(data) {
    return request({
        url: '/asset/detail',
        method: 'post',
        data: data
    })
}

// 修改资产详情
export function updateDetail(data) {
    return request({
        url: '/asset/detail',
        method: 'put',
        data: data
    })
}

// 删除资产详情
export function delDetail(id) {
    return request({
        url: '/asset/detail/' + id,
        method: 'delete'
    })
}
