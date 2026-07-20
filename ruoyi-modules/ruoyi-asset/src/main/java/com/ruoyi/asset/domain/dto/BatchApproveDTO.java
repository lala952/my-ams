package com.ruoyi.asset.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 资产变动批量审批 DTO
 */
@Data
public class BatchApproveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单据ID列表
     */
    private List<Long> ids;

    /**
     * 审批结果（同意-true，驳回-false）
     */
    private Boolean result;

    /**
     * 审批意见
     */
    private String comment;
}