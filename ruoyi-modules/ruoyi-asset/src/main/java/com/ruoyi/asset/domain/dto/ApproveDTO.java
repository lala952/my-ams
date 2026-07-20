package com.ruoyi.asset.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 资产变动审批 DTO
 */
@Data
public class ApproveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单据ID
     */
    private Long id;

    /**
     * 审批结果（同意-true，驳回-false）
     */
    private Boolean result;

    /**
     * 审批意见
     */
    private String comment;
}