import request from '@/utils/request'

// 查询资产报废列表
export function listScrap(query) {
    return request({
        url: '/asset/scrap/list',
        method: 'get',
        params: query
    })
}

// 查询资产报废详细
export function getScrap(id) {
    return request({
        url: '/asset/scrap/' + id,
        method: 'get'
    })
}

// 新增资产报废
export function addScrap(data) {
    return request({
        url: '/asset/scrap',
        method: 'post',
        data: data
    })
}

// 修改资产报废
export function updateScrap(data) {
    return request({
        url: '/asset/scrap',
        method: 'put',
        data: data
    })
}

// 删除资产报废
export function delScrap(id) {
    return request({
        url: '/asset/scrap/' + id,
        method: 'delete'
    })
}
