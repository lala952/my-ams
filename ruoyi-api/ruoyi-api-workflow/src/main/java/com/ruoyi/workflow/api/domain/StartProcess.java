package com.ruoyi.workflow.api.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class StartProcess implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 流程定义键，如，asset_change_process
     */
    private String processKey;
    /**
     * 业务键,关联业务数据的编号，如change.id
     */
    private String businessKey;
    /**
     * 流程变量,传给流程的参数
     */
    private Map<String, Object> variables;
}
