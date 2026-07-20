package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 资产盘点明细对象 asset_inventory_detail
 *
 * @author xiaowang
 * @date 2026-04-02
 */
@Data
@TableName("asset_inventory_detail")
public class InventoryDetail {

    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    private Long id;

    /**
     * 关联主表ID
     */
    private Long masterId;

    /**
     * 资产ID
     */
    private Long assetId;

    /**
     * 账面数量
     */
    private Integer theoreticalQuantity;

    /**
     * 实盘数量
     */
    private Integer actualQuantity;

    /**
     * 差异数量
     */
    private Integer difference;

    /**
     * 盘点结果
     */
    private String inventoryResult;

    /**
     * 盘点时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date inventoryTime;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}