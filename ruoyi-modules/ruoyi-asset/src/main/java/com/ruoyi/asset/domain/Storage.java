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
 * 资产入库对象 asset_storage
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_storage")
@EqualsAndHashCode(callSuper = true)
public class Storage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 入库ID
     */
    private Long id;

    /**
     * 入库单号
     */
    @Excel(name = "入库单号", width = 50, defaultValue = "无")
    private String storageCode;

    /**
     * 入库类型
     */
    @Excel(name = "入库类型", defaultValue = "无")
    private String storageType;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请部门ID
     */
    private Long applicantDeptId;

    /**
     * 申请日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申请日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date applyDate;

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
     * 总金额
     */
    @Excel(name = "总金额", defaultValue = "无")
    private BigDecimal totalAmount;

    /**
     * 资产数量
     */
    @Excel(name = "资产数量", defaultValue = "无")
    private Integer assetCount;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
