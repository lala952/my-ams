package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.InventoryDetail;

import java.util.List;

/**
 * 资产盘点明细Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface InventoryDetailMapper extends BaseMapper<InventoryDetail> {
    /**
     * 查询资产盘点明细列表
     *
     * @param inventoryDetail 资产盘点明细
     * @return 资产盘点明细集合
     */
    public List<InventoryDetail> selectInventoryDetailList(InventoryDetail inventoryDetail);
}
