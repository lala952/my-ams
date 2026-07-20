package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 资产计划对象 asset_plan
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_plan")
@EqualsAndHashCode(callSuper = true)
public class Plan extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 计划ID
     */
    private Long id;

    /**
     * 计划编号
     */
    @Excel(name = "计划编号")
    private String planCode;

    /**
     * 计划名称
     */
    @Excel(name = "计划名称")
    private String planName;

    /**
     * 年度
     */
    @Excel(name = "年度")
    private Integer year;

    /**
     * 编制部门ID
     */
    private Long deptId;

    /**
     * 编制人ID
     */
    private Long userId;

    /**
     * 预算总额
     */
    @Excel(name = "预算总额")
    private java.math.BigDecimal totalBudget;

    /**
     * 业务状态（draft草稿 submitted已提交 approved已批准 rejected已驳回 cancelled已取消）
     */
    @Excel(name = "业务状态")
    private String businessStatus;

    /**
     * 流程实例ID
     */
    private String procInstId;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 计划明细列表
     */
    @TableField(exist = false)
    private List<PlanDetail> details;
}
