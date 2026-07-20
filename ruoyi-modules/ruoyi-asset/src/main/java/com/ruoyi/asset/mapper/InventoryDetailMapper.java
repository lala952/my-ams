package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.InventoryDetail;

import java.util.List;

public interface InventoryDetailMapper extends BaseMapper<InventoryDetail> {
    public List<InventoryDetail> selectInventoryDetailList(InventoryDetail inventoryDetail);
}
