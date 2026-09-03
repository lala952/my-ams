package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Purchase;

import java.util.List;

/**
 * 资产采购Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface PurchaseMapper extends BaseMapper<Purchase> {
    /**
     * 查询资产采购列表
     *
     * @param purchase 资产采购
     * @return 资产采购集合
     */
    public List<Purchase> selectPurchaseList(Purchase purchase);
}
