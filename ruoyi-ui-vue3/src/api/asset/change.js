import request from '@/utils/request'

// 查询资产变动列表
export function listChange(query) {
    return request({
        url: '/asset/change/list',
        method: 'get',
        params: query
    })
}

// 查询资产变动详细
export function getChange(id) {
    return request({
        url: '/asset/change/' + id,
        method: 'get'
    })
}

// 新增资产变动
export function addChange(data) {
    return request({
        url: '/asset/change',
        method: 'post',
        data: data
    })
}

// 修改资产变动
export function updateChange(data) {
    return request({
        url: '/asset/change',
        method: 'put',
        data: data
    })
}

// 删除资产变动
export function delChange(id) {
    return request({
        url: '/asset/change/' + id,
        method: 'delete'
    })
}

// 提交资产变动（启动工作流）
export function submitChange(data) {
    return request({
        url: '/asset/change/submit',
        method: 'post',
        data: data
    })
}

// 审批资产变动
export function approveChange(data) {
    return request({
        url: '/asset/change/approve',
        method: 'post',
        data: data
    })
}

// 批量审批
export function batchApprove(data) {
    return request({
        url: '/asset/change/batchApprove',
        method: 'post',
        data: data
    })
}

// 统计各状态数量
export function countByStatus() {
    return request({
        url: '/asset/change/countByStatus',
        method: 'get'
    })
}

// 撤回申请
export function withdrawChange(id) {
    return request({
        url: '/asset/change/withdraw/' + id,
        method: 'post'
    })
}

// 获取流程定义XML（用于流程图渲染）
export function getProcessXml(procDefId) {
    return request({
        url: '/workflow/process/xml/' + procDefId,
        method: 'get'
    })
}

// 获取流程状态（用于高亮节点）
export function getProcessStatus(procInstId) {
    return request({
        url: '/workflow/process/status/' + procInstId,
        method: 'get'
    })
}

// 获取审批历史记录
export function getApprovalHistory(procInstId) {
    return request({
        url: '/workflow/history/approval/' + procInstId,
        method: 'get'
    })
}

// PDF导出接口
export function exportChangePdf(id) {
    return request({
        url: '/asset/change/pdf/' + id,
        method: 'get',
        responseType: 'blob'
    })
}

// 保存草稿到数据库
export function saveDraft(data) {
    return request({
        url: '/asset/change/draft/save',
        method: 'post',
        data: data
    })
}
