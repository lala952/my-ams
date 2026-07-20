package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Purchase;

import java.util.List;

public interface PurchaseMapper extends BaseMapper<Purchase> {
    public List<Purchase> selectPurchaseList(Purchase purchase);
}
