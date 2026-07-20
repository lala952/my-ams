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
 * 资产申购对象 asset_purchase
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_purchase")
@EqualsAndHashCode(callSuper = true)
public class Purchase extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 申购ID
     */
    private Long id;

    /**
     * 申购编码
     */
    @Excel(name = "申购编码", width = 50, defaultValue = "无")
    private String purchaseCode;

    /**
     * 申购名称
     */
    @Excel(name = "申购名称", width = 50, defaultValue = "无")
    private String purchaseName;

    /**
     * 资产类型
     */
    @Excel(name = "资产类型", defaultValue = "无")
    private String assetType;

    /**
     * 申购部门ID
     */
    private Long deptId;

    /**
     * 申购人ID
     */
    private Long applicantId;

    /**
     * 规格型号
     */
    @Excel(name = "规格型号", defaultValue = "无")
    private String model;

    /**
     * 品牌
     */
    @Excel(name = "品牌", defaultValue = "无")
    private String brand;

    /**
     * 技术参数
     */
    @Excel(name = "技术参数", defaultValue = "无")
    private String spec;

    /**
     * 申购数量
     */
    @Excel(name = "申购数量", defaultValue = "无")
    private Integer quantity;

    /**
     * 预计单价
     */
    @Excel(name = "预计单价", defaultValue = "无")
    private BigDecimal estimatedPrice;

    /**
     * 申购用途
     */
    @Excel(name = "申购用途", defaultValue = "无")
    private String purpose;

    /**
     * 申购原因
     */
    @Excel(name = "申购原因", defaultValue = "无")
    private String reason;

    /**
     * 申购日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申购日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date applyDate;

    /**
     * 期望到货日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "期望到货日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date expectedDate;

    /**
     * 业务状态：draft-草稿，submitted-已提交，approved-已批准，rejected-已驳回
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
