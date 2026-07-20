import request from '@/utils/request'

// 查询资产折旧列表
export function listDepreciation(query) {
    return request({
        url: '/asset/depreciation/list',
        method: 'get',
        params: query
    })
}

// 查询资产折旧详细
export function getDepreciation(id) {
    return request({
        url: '/asset/depreciation/' + id,
        method: 'get'
    })
}

// 新增资产折旧
export function addDepreciation(data) {
    return request({
        url: '/asset/depreciation',
        method: 'post',
        data: data
    })
}

// 修改资产折旧
export function updateDepreciation(data) {
    return request({
        url: '/asset/depreciation',
        method: 'put',
        data: data
    })
}

// 删除资产折旧
export function delDepreciation(id) {
    return request({
        url: '/asset/depreciation/' + id,
        method: 'delete'
    })
}
