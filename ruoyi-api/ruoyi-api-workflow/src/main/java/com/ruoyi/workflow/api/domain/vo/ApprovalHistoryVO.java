package com.ruoyi.workflow.api.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.workflow.api.constant.NodeStatusConstants;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 审批历史 VO
 */
@Data
public class ApprovalHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务定义Key
     */
    private String taskDefinitionKey;

    /**
     * 办理人ID
     */
    private String assignee;

    /**
     * 办理人姓名
     */
    private String assigneeName;

    /**
     * 任务开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 任务结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 审批结果（true-同意，false-拒绝）
     */
    private Boolean approved;

    /**
     * 耗时（毫秒）
     */
    private Long durationInMillis;

    /**
     * 节点状态：completed-已完成, active-办理中, pending-未处理
     * 注意：此字段表示单个审批节点的状态，与 BusinessStatus（整个单据的业务状态）不同
     * 使用常量：{@link NodeStatusConstants}
     */
    private String nodeStatus;

}
