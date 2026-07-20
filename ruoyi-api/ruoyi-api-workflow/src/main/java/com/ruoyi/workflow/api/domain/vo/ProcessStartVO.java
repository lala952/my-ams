package com.ruoyi.workflow.api.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 流程启动响应 VO
 */
@Data
@NoArgsConstructor
public class ProcessStartVO implements Serializable {

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
     * 业务键（关联业务数据）
     */
    private String businessKey;

    /**
     * 流程是否已启动，true-已启动，false-未启动
     */
    private Boolean started;

    public ProcessStartVO(String procInstId, String procDefId, String businessKey) {
        this.procInstId = procInstId;
        this.procDefId = procDefId;
        this.businessKey = businessKey;
        this.started = true;
    }
}