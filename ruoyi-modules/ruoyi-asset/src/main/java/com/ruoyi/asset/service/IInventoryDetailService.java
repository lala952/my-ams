package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.InventoryDetail;

import java.util.List;

/**
 * 资产盘点明细Service接口
 *
 * @author xiaowang
 */
public interface IInventoryDetailService extends IService<InventoryDetail> {


    /**
     * 查询盘点明细列表
     */
    List<InventoryDetail> selectInventoryDetailList(InventoryDetail inventoryDetail);
}