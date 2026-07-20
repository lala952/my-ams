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
 * 资产台账对象 asset_assets
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_assets")
@EqualsAndHashCode(callSuper = true)
public class Assets extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 资产ID
     */
    private Long id;

    /**
     * 资产编码
     */
    @Excel(name = "资产编码", width = 30, defaultValue = "无")
    private String assetCode;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 使用部门ID
     */
    private Long deptId;

    /**
     * 使用人ID
     */
    private Long userId;

    /**
     * 资产名称
     */
    @Excel(name = "资产名称", width = 30, defaultValue = "无")
    private String assetName;

    /**
     * 资产类型
     */
    @Excel(name = "资产类型", defaultValue = "无")
    private String assetType;

    /**
     * 资产来源
     */
    @Excel(name = "资产来源", defaultValue = "无")
    private String assetSource;

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
     * 序列号
     */
    @Excel(name = "序列号", width = 30, defaultValue = "无")
    private String serialNumber;

    /**
     * 单位
     */
    @Excel(name = "单位", defaultValue = "无")
    private String unit;

    /**
     * 存放位置
     */
    @Excel(name = "存放位置", width = 30, defaultValue = "无")
    private String location;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 资产状态
     */
    @Excel(name = "资产状态", defaultValue = "无")
    private String assetStatus;

    /**
     * 条码
     */
    @Excel(name = "条码", defaultValue = "无")
    private String barcode;

    /**
     * RFID标签
     */
    @Excel(name = "RFID标签", defaultValue = "无")
    private String rfidTag;

    /**
     * 数量
     */
    @Excel(name = "数量", defaultValue = "0")
    private Long quantity;

    /**
     * 原值，用户输入
     */
    @Excel(name = "原值", defaultValue = "0.00")
    private BigDecimal originalValue;

    /**
     * 残值，=原值  * 残值率
     */
    @Excel(name = "残值", defaultValue = "0.00")
    private BigDecimal salvageValue;

    /**
     * 折旧方法
     */
    @Excel(name = "折旧方法", defaultValue = "无")
    private String depreciationMethod;

    /**
     * 折旧率，=月 / 年折旧率
     */
    @Excel(name = "折旧率", defaultValue = "无")
    private BigDecimal depreciationRate;

    /**
     * 累计折旧，月折旧额 * 已折旧月数
     */
    @Excel(name = "累计折旧", defaultValue = "无")
    private BigDecimal accumulatedDepreciation;

    /**
     * 净值，= 原值 - 累计折旧
     */
    @Excel(name = "净值", defaultValue = "无")
    private BigDecimal netValue;

    /**
     * 开始折旧日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始折旧日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date depreciationStartDate;

    /**
     * 购置日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "购置日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date purchaseDate;

    /**
     * 采购价格
     */
    @Excel(name = "采购价格", defaultValue = "0.00")
    private BigDecimal purchasePrice;

    /**
     * 保修截止日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "保修截止日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date warrantyExpireDate;

    /**
     * 生产日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生产日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date manufactureDate;

    /**
     * 开始使用日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始使用日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date startUseDate;

    /**
     * 报废日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报废日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date scrapDate;

    /**
     * 主图片
     */
    private String mainImage;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
