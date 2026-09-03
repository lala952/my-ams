package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 资产移交对象 asset_handover
 *
 * @author ruoyi
 * @date 2026-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("asset_handover")
public class Handover extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 移交ID */
    @TableId(value = "id")
    private Long id;

    /** 移交单号 */
    private String handoverCode;

    /** 资产ID */
    private Long assetId;

    /** 移交人ID */
    private Long fromPersonId;

    /** 接收人ID */
    private Long toPersonId;

    /** 移交日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date handoverDate;

    /** 业务状态 */
    private String businessStatus;

    /** 流程实例ID */
    private String procInstId;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
