import request from '@/utils/request.js'

/**
 * 获取流程定义XML（根据流程定义ID）
 * @param {String} procDefId - 流程定义ID
 */
export function getProcessXml(procDefId) {
    return request({
        url: '/workflow/process/xml/' + procDefId,
        method: 'get'
    })
}

/**
 * 根据流程Key获取XML
 * @param {String} processKey - 流程Key
 */
export function getProcessXmlByKey(processKey) {
    return request({
        url: '/workflow/process/xmlByKey/' + processKey,
        method: 'get'
    })
}

/**
 * 获取流程实例状态（用于流程图高亮）
 * @param {String} procInstId - 流程实例ID
 */
export function getProcessStatus(procInstId) {
    return request({
        url: '/workflow/process/status/' + procInstId,
        method: 'get'
    })
}

/**
 * 获取审批历史
 * @param {String} procInstId - 流程实例ID
 */
export function getApprovalHistory(procInstId) {
    return request({
        url: '/workflow/history/approval/' + procInstId,
        method: 'get'
    })
}

/**
 * 获取当前任务信息
 * @param {String} procInstId - 流程实例ID
 */
export function getCurrentTask(procInstId) {
    return request({
        url: '/workflow/task/current/' + procInstId,
        method: 'get'
    })
}

/**
 * 审批任务（通过/驳回）
 * @param {Object} data - 审批数据
 * @param {String} data.taskId - 任务ID
 * @param {Boolean} data.approved - 是否通过
 * @param {String} data.comment - 审批意见
 */
export function approveTask(data) {
    return request({
        url: '/workflow/task/complete',
        method: 'post',
        data: data
    })
}

/**
 * 检查流程是否已结束
 * @param {String} procInstId - 流程实例ID
 */
export function isProcessEnded(procInstId) {
    return request({
        url: '/workflow/process/isEnded/' + procInstId,
        method: 'get'
    })
}

/**
 * 查询个人待办任务列表
 */
export function getTodoTaskList() {
    return request({
        url: '/workflow/task/todoList',
        method: 'get'
    })
}