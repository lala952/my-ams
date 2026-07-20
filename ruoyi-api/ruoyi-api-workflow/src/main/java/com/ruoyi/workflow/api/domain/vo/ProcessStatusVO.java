package com.ruoyi.workflow.api.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 流程状态 VO
 */
@Data
public class ProcessStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 流程名称
     */
    private String processName;

    /**
     * 业务键
     */
    private String businessKey;

    /**
     * 流程是否已结束
     */
    private Boolean processEnded;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 当前活跃节点ID列表
     */
    private List<String> activeActivityIds;

    /**
     * 已完成节点Key列表
     */
    private List<String> finishedActivityIds;

    /**
     * 流程发起人ID
     */
    private Long initiator;

    /**
     * 启动时间
     */
    private Date startTime;
}
