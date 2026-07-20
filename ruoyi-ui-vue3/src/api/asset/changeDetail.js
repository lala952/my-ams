import request from '@/utils/request'

// 查询资产变动明细列表
export function listChangeDetail(query) {
    return request({
        url: '/asset/changeDetail/list',
        method: 'get',
        params: query
    })
}

// 查询资产变动明细详细
export function getChangeDetail(id) {
    return request({
        url: '/asset/changeDetail/' + id,
        method: 'get'
    })
}

// 新增资产变动明细
export function addChangeDetail(data) {
    return request({
        url: '/asset/changeDetail',
        method: 'post',
        data: data
    })
}

// 修改资产变动明细
export function updateChangeDetail(data) {
    return request({
        url: '/asset/changeDetail',
        method: 'put',
        data: data
    })
}

// 删除资产变动明细
export function delChangeDetail(id) {
    return request({
        url: '/asset/changeDetail/' + id,
        method: 'delete'
    })
}
