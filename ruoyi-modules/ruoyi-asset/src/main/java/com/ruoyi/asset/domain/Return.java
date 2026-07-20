package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 资产归还对象 asset_return
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_return")
@EqualsAndHashCode(callSuper = true)
public class Return extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 归还ID
     */
    private Long id;

    /**
     * 归还单号
     */
    @Excel(name = "归还单号", width = 50, defaultValue = "无")
    private String returnCode;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 归还人ID
     */
    private Long returnPersonId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 归还日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "归还日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date returnDate;

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
