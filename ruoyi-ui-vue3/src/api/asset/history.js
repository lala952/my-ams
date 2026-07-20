import request from '@/utils/request'

// 查询资产履历列表
export function listHistory(query) {
    return request({
        url: '/asset/history/list',
        method: 'get',
        params: query
    })
}

// 查询资产履历详细
export function getHistory(id) {
    return request({
        url: '/asset/history/' + id,
        method: 'get'
    })
}

// 新增资产履历
export function addHistory(data) {
    return request({
        url: '/asset/history',
        method: 'post',
        data: data
    })
}

// 修改资产履历
export function updateHistory(data) {
    return request({
        url: '/asset/history',
        method: 'put',
        data: data
    })
}

// 删除资产履历
export function delHistory(id) {
    return request({
        url: '/asset/history/' + id,
        method: 'delete'
    })
}
