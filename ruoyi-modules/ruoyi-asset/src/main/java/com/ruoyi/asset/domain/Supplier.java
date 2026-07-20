package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 资产供应商对象 asset_supplier
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_supplier")
@EqualsAndHashCode(callSuper = true)
public class Supplier extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 供应商ID
     */
    private Long id;

    /**
     * 供应商编码
     */
    @Excel(name = "供应商编码")
    private String supplierCode;

    /**
     * 供应商名称
     */
    @Excel(name = "供应商名称")
    private String supplierName;

    /**
     * 简称
     */
    @Excel(name = "简称")
    private String shortName;

    /**
     * 联系人
     */
    @Excel(name = "联系人")
    private String contactPerson;

    /**
     * 联系电话
     */
    @Excel(name = "联系电话")
    private String contactPhone;

    /**
     * 邮箱
     */
    @Excel(name = "邮箱")
    private String contactEmail;

    /**
     * 地址
     */
    @Excel(name = "地址")
    private String address;

    /**
     * 供应商类型
     */
    @Excel(name = "供应商类型", defaultValue = "无")
    private String supplierType;

    /**
     * 信用等级
     */
    @Excel(name = "信用等级", defaultValue = "无")
    private String creditRating;

    /**
     * 合作状态
     */
    @Excel(name = "合作状态", defaultValue = "无")
    private String cooperationStatus;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
