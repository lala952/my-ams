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
 * 资产转固对象 asset_capitalize
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_capitalize")
@EqualsAndHashCode(callSuper = true)
public class Capitalize extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 转固ID
     */
    private Long id;

    /**
     * 转固编码
     */
    @Excel(name = "转固编码", width = 50, defaultValue = "无")
    private String capitalizeCode;

    /**
     * 验收ID
     */
    private Long acceptanceId;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 转固日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "转固日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date capitalizeDate;

    /**
     * 转固原值
     */
    @Excel(name = "转固原值")
    private BigDecimal originalValue;

    /**
     * 开始折旧日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始折旧日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date depreciationStartDate;

    /**
     * 业务状态：pending-待转固，completed-已完成
     */
    @Excel(name = "业务状态", defaultValue = "无")
    private String businessStatus;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
