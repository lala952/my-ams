package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 资产处置对象 asset_disposal
 *
 * @author ruoyi
 * @date 2026-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("asset_disposal")
public class Disposal extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 处置ID */
    @TableId(value = "id")
    private Long id;

    /** 处置单号 */
    private String disposalCode;

    /** 资产ID */
    private Long assetId;

    /** 处置类型 */
    private String disposalType;

    /** 处置日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date disposalDate;

    /** 业务状态 */
    private String businessStatus;

    /** 流程实例ID */
    private String procInstId;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
