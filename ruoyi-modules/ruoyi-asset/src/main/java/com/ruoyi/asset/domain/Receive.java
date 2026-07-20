package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 资产领用对象 asset_receive
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_receive")
@EqualsAndHashCode(callSuper = true)
public class Receive extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 领用ID
     */
    private Long id;

    /**
     * 领用单号
     */
    @Excel(name = "领用单号", width = 50, defaultValue = "无")
    private String receiveCode;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 领用人ID
     */
    private Long receivePersonId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 领用日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "领用日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date receiveDate;

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
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
