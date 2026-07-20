import request from '@/utils/request'

// 查询资产拆分明细列表
export function listSplit(query) {
    return request({
        url: '/asset/split/list',
        method: 'get',
        params: query
    })
}

// 查询资产拆分明细详细
export function getSplit(id) {
    return request({
        url: '/asset/split/' + id,
        method: 'get'
    })
}

// 新增资产拆分明细
export function addSplit(data) {
    return request({
        url: '/asset/split',
        method: 'post',
        data: data
    })
}

// 修改资产拆分明细
export function updateSplit(data) {
    return request({
        url: '/asset/split',
        method: 'put',
        data: data
    })
}

// 删除资产拆分明细
export function delSplit(id) {
    return request({
        url: '/asset/split/' + id,
        method: 'delete'
    })
}
