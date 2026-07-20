package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Inventory;
import com.ruoyi.asset.domain.InventoryDetail;

import java.util.List;

public interface InventoryMapper extends BaseMapper<Inventory> {
    public List<Inventory> selectInventoryList(Inventory inventory);

    public int deleteInventoryDetailByMasterIds(Long[] ids);

    public int batchInventoryDetail(List<InventoryDetail> inventoryDetailList);

    public int deleteInventoryDetailByMasterId(Long id);
}
