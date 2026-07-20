import request from '@/utils/request'

// 查询资产附件列表
export function listAttachment(query) {
    return request({
        url: '/asset/attachment/list',
        method: 'get',
        params: query
    })
}

// 查询资产附件详细
export function getAttachment(id) {
    return request({
        url: '/asset/attachment/' + id,
        method: 'get'
    })
}

// 新增资产附件
export function addAttachment(data) {
    return request({
        url: '/asset/attachment',
        method: 'post',
        data: data
    })
}

// 修改资产附件
export function updateAttachment(data) {
    return request({
        url: '/asset/attachment',
        method: 'put',
        data: data
    })
}

// 删除资产附件
export function delAttachment(id) {
    return request({
        url: '/asset/attachment/' + id,
        method: 'delete'
    })
}
