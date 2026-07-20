package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 资产变动明细表（只存储关联关系）
 *
 * @author xiaowang
 * @date 2026-05-12
 */
@Data
@TableName("asset_change_detail")
public class ChangeDetail {

    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    private Long id;

    /**
     * 变动单ID
     */
    private Long masterId;

    /**
     * 资产ID
     */
    private Long assetId;
}
