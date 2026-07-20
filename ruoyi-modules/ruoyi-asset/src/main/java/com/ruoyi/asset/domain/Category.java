package com.ruoyi.asset.domain;

import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.TreeEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 资产分类对象 asset_category
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@Data
public class Category extends TreeEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类编码
     */
    @Excel(name = "分类编码")
    private String categoryCode;

    /**
     * 分类名称
     */
    @Excel(name = "分类名称")
    private String categoryName;

    /**
     * 层级
     */
    @Excel(name = "层级")
    private Long level;

    /**
     * 父分类ID
     */
    @Excel(name = "父分类id")
    private Long parentId;

    /**
     * 排序
     */
    @Excel(name = "排序")
    private Long sortOrder;

    /**
     * 折旧年限(年)
     */
    @Excel(name = "折旧年限(年)")
    private Integer depreciationYears;

    /**
     * 折旧方法
     */
    @Excel(name = "折旧方法")
    private String depreciationMethod;

    /**
     * 残值率(%)
     */
    @Excel(name = "残值率(%)")
    private BigDecimal salvageRate;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
