package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 资产盘点对象 asset_inventory
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_inventory")
@EqualsAndHashCode(callSuper = true)
public class Inventory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 盘点ID
     */
    private Long id;

    /**
     * 盘点编码
     */
    @Excel(name = "盘点编码", width = 50, defaultValue = "无")
    private String inventoryCode;

    /**
     * 盘点名称
     */
    @Excel(name = "盘点名称", width = 50, defaultValue = "无")
    private String inventoryName;

    /**
     * 盘点类型
     */
    @Excel(name = "盘点类型", defaultValue = "无")
    private String inventoryType;

    /**
     * 盘点部门ID
     */
    private Long deptId;

    /**
     * 盘点人ID
     */
    private Long inventoryPersonId;

    /**
     * 盘点日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "盘点日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date inventoryDate;

    /**
     * 计划开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划开始日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date plannedStartDate;

    /**
     * 计划结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "计划结束日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date plannedEndDate;

    /**
     * 实际开始日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "实际开始日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date actualStartDate;

    /**
     * 实际结束日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "实际结束日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date actualEndDate;

    /**
     * 业务状态：pending-待盘点，processing-盘点中，completed-已完成
     */
    @Excel(name = "业务状态", defaultValue = "无")
    private String businessStatus;

    /**
     * 总资产数
     */
    @Excel(name = "总资产数", defaultValue = "无")
    private Long totalAssets;

    /**
     * 已盘点数
     */
    @Excel(name = "已盘点数", defaultValue = "无")
    private Long inventoriedCount;

    /**
     * 盘盈数
     */
    @Excel(name = "盘盈数", defaultValue = "无")
    private Long surplusCount;

    /**
     * 盘亏数
     */
    @Excel(name = "盘亏数", defaultValue = "无")
    private Long shortageCount;

    /**
     * 完成率
     */
    @Excel(name = "完成率", defaultValue = "无")
    private java.math.BigDecimal completionRate;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 资产盘点明细信息
     */
    @TableField(exist = false)
    private java.util.List<InventoryDetail> inventoryDetailList;
}
