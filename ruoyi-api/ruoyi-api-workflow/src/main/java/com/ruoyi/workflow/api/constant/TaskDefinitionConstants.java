package com.ruoyi.workflow.api.constant;

/**
 * 流程任务定义常量
 *
 * @author xiaowang
 * @date 2026-05-15
 */
public class TaskDefinitionConstants {
    /**
     * 经办人提交/提交申请
     */
    public static final String SUBMIT = "submit";
    
    /**
     * 经办人重新提交（驳回后修改再提交）
     */
    public static final String RESUBMIT = "resubmit";
    
    /**
     * 管理员审批
     */
    public static final String MANAGER_APPROVE = "managerApprove";
}
