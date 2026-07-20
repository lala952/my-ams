package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 资产变动对象 asset_change
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_change")
@EqualsAndHashCode(callSuper = true)
public class Change extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 变动ID
     */
    private Long id;

    /**
     * 变动单编码
     */
    @Excel(name = "变动单编码", width = 30, defaultValue = "无")
    private String changeCode;

    /**
     * 变动类型
     */
    @Excel(name = "变动类型", defaultValue = "无")
    private String changeType;

    /**
     * 变动原因
     */
    @Excel(name = "变动原因", defaultValue = "无")
    private String changeReason;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 申请日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "申请日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date applyDate;

    /**
     * 流程实例ID
     */
    private String procInstId;

    /**
     * 业务状态（draft-草稿，pending-办理中，rejected-已退回，completed-已完成）
     */
    @Excel(name = "审批状态", defaultValue = "无")
    private String businessStatus;

    @Excel(name = "备注", defaultValue = "无")
    private String remark;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
