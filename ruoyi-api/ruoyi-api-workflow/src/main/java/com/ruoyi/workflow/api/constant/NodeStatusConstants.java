package com.ruoyi.workflow.api.constant;

/**
 * 审批节点状态常量
 * 用于表示工作流中单个审批节点的状态
 * 
 * @author ruoyi
 */
public class NodeStatusConstants {

    /**
     * 已完成 - 该节点已完成审批
     */
    public static final String COMPLETED = "completed";

    /**
     * 办理中 - 该节点正在办理（当前待办任务）
     */
    public static final String ACTIVE = "active";

    /**
     * 未处理 - 该节点还未到达（后续节点）
     */
    public static final String PENDING = "pending";

    private NodeStatusConstants() {
        // 私有构造函数，防止实例化
    }
}
