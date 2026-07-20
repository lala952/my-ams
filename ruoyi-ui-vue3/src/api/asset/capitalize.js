import request from '@/utils/request'

// 查询资产转固列表
export function listCapitalize(query) {
    return request({
        url: '/asset/capitalize/list',
        method: 'get',
        params: query
    })
}

// 查询资产转固详细
export function getCapitalize(id) {
    return request({
        url: '/asset/capitalize/' + id,
        method: 'get'
    })
}

// 新增资产转固
export function addCapitalize(data) {
    return request({
        url: '/asset/capitalize',
        method: 'post',
        data: data
    })
}

// 修改资产转固
export function updateCapitalize(data) {
    return request({
        url: '/asset/capitalize',
        method: 'put',
        data: data
    })
}

// 删除资产转固
export function delCapitalize(id) {
    return request({
        url: '/asset/capitalize/' + id,
        method: 'delete'
    })
}
