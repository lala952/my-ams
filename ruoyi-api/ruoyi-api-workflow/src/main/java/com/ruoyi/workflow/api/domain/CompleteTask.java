package com.ruoyi.workflow.api.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class CompleteTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */

    private String taskId;
    /**
     * 执行任务完成操作的用户ID
     */
    private Long userId;

    /**
     * 审批结果(true-同意，false-拒绝)
     */
    private Boolean approved;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 流程变量
     */
    private Map<String, Object> variables;
}
