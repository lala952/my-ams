package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Inventory;
import com.ruoyi.asset.domain.InventoryDetail;

import java.util.List;

/**
 * 资产盘点Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface InventoryMapper extends BaseMapper<Inventory> {
    /**
     * 查询资产盘点列表
     *
     * @param inventory 资产盘点
     * @return 资产盘点集合
     */
    public List<Inventory> selectInventoryList(Inventory inventory);

    /**
     * 批量删除盘点明细
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteInventoryDetailByMasterIds(Long[] ids);

    /**
     * 批量新增盘点明细
     *
     * @param inventoryDetailList 盘点明细集合
     * @return 结果
     */
    public int batchInventoryDetail(List<InventoryDetail> inventoryDetailList);

    /**
     * 根据盘点主键删除明细
     *
     * @param id 盘点主键
     * @return 结果
     */
    public int deleteInventoryDetailByMasterId(Long id);
}
