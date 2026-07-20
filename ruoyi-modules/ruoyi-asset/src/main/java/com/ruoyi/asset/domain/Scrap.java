package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 资产报废对象 asset_scrap
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_scrap")
@EqualsAndHashCode(callSuper = true)
public class Scrap extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 报废ID
     */
    private Long id;

    /**
     * 报废编码
     */
    @Excel(name = "报废编码", width = 50, defaultValue = "无")
    private String scrapCode;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 报废日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "报废日期", width = 20, dateFormat = "yyyy-MM-dd", defaultValue = "无")
    private Date scrapDate;

    /**
     * 报废原因
     */
    @Excel(name = "报废原因", defaultValue = "无")
    private String scrapReason;

    /**
     * 报废类型
     */
    @Excel(name = "报废类型", defaultValue = "无")
    private String scrapType;

    /**
     * 业务状态：draft-草稿，pending-待审批，approved-已批准，rejected-已退回
     */
    @Excel(name = "业务状态", defaultValue = "无")
    private String businessStatus;

    /**
     * 审批人ID
     */
    private Long approverId;

    /**
     * 审批时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
