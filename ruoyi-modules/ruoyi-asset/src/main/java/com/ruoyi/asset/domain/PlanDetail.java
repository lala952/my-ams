package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 资产配置计划明细对象 asset_plan_detail
 *
 * @author xiaowang
 * @date 2026-05-07
 */
@Data
@TableName("asset_plan_detail")
@EqualsAndHashCode(callSuper = true)
public class PlanDetail extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    private Long id;

    /**
     * 主表ID
     */
    private Long masterId;

    /**
     * 资产分类ID
     */
    private Long categoryId;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 规格型号
     */
    private String model;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 预估单价
     */
    private BigDecimal estimatedPrice;

    /**
     * 预估总价
     */
    private BigDecimal totalPrice;

    /**
     * 删除标识
     */
    private String delFlag;
}
