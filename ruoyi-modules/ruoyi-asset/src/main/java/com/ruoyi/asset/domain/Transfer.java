package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 资产调拨对象 asset_transfer
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_transfer")
@EqualsAndHashCode(callSuper = true)
public class Transfer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 调拨ID
     */
    private Long id;

    /**
     * 调拨单号
     */
    @Excel(name = "调拨单号", width = 50, defaultValue = "无")
    private String transferCode;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 调出部门ID
     */
    private Long fromDeptId;

    /**
     * 调入部门ID
     */
    private Long toDeptId;

    /**
     * 调拨日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "调拨日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date transferDate;

    /**
     * 调拨原因
     */
    @Excel(name = "调拨原因", defaultValue = "无")
    private String transferReason;

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
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
