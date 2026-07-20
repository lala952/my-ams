package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 资产折旧记录对象 asset_depreciation
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_depreciation")
@EqualsAndHashCode(callSuper = true)
public class Depreciation extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 折旧期间
     */
    private String depreciationPeriod;

    /**
     * 折旧日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date depreciationDate;

    /**
     * 年份
     */
    private Integer periodYear;

    /**
     * 月份
     */
    private Integer periodMonth;

    /**
     * 折旧金额
     */
    private BigDecimal depreciationAmount;

    /**
     * 累计折旧
     */
    private BigDecimal accumulatedDepreciation;

    /**
     * 净值
     */
    private BigDecimal netValue;

    /**
     * 状态
     */
    private String businessStatus;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
