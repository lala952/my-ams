package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Inventory;
import com.ruoyi.asset.domain.InventoryDetail;

import java.util.List;

/**
 * 资产盘点管理Service接口
 *
 * @author xiaowang
 */
public interface IInventoryService extends IService<Inventory> {


    /**
     * 查询盘点任务列表
     */
    List<Inventory> selectInventoryList(Inventory inventory);

    //  明细操作接口 

    /**
     * 根据主表ID数组删除盘点明细
     */
    int deleteInventoryDetailByMasterIds(Long[] ids);

    /**
     * 根据主表ID删除盘点明细
     */
    int deleteInventoryDetailByMasterId(Long id);

    /**
     * 批量新增盘点明细
     */
    int batchInventoryDetail(List<InventoryDetail> inventoryDetailList);
}