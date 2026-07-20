package com.ruoyi.workflow.api.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 待办任务 VO
 */
@Data
public class TodoTaskVO implements Serializable {

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
     * 任务创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 流程实例ID
     */
    private String procInstId;

    /**
     * 流程定义ID
     */
    private String procDefId;

    /**
     * 流程定义Key
     */
    private String processDefinitionKey;

    /**
     * 业务Key（关联的业务单据ID）
     */
    private String businessKey;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 持续时间（毫秒）
     */
    private Long durationInMillis;
}
