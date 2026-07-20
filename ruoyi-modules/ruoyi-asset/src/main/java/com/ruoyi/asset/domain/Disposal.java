package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("asset_disposal")
public class Disposal extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    private String disposalCode;

    private Long assetId;

    private String disposalType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date disposalDate;

    private String businessStatus;

    private String procInstId;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
