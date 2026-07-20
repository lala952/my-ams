package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 资产维修对象 asset_maintain
 *
 * @author xiaowang
 * @date 2026-05-14
 */
@Data
@TableName("asset_maintain")
@EqualsAndHashCode(callSuper = true)
public class Maintain extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 维修ID
     */
    private Long id;

    /**
     * 维修单号
     */
    @Excel(name = "维修单号", width = 50, defaultValue = "无")
    private String maintenanceCode;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 维修类型
     */
    @Excel(name = "维修类型", defaultValue = "无")
    private String maintenanceType;

    /**
     * 故障描述
     */
    @Excel(name = "故障描述", defaultValue = "无")
    private String faultDescription;

    /**
     * 维修内容
     */
    @Excel(name = "维修内容", defaultValue = "无")
    private String maintainContent;

    /**
     * 维修日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "维修日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date maintenanceDate;

    /**
     * 维修人ID
     */
    private Long maintainPersonId;

    /**
     * 维修费用
     */
    @Excel(name = "维修费用", defaultValue = "无")
    private BigDecimal maintainCost;

    /**
     * 业务状态：draft-待提交, pending-办理中, rejected-已退回, completed-已完成
     */
    @Excel(name = "业务状态", defaultValue = "无")
    private String businessStatus;

    /**
     * 流程实例ID
     */
    private String procInstId;

    /**
     * 流程定义ID
     */
    private String procDefId;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}