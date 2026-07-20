package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 资产履历对象 asset_history
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_history")
public class History {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件详情
     */
    private String eventDetail;

    /**
     * 变更前数据
     */
    private String beforeData;

    /**
     * 变更后数据
     */
    private String afterData;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date operateTime;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

}
