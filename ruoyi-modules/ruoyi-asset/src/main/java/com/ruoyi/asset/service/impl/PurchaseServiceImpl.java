package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Purchase;
import com.ruoyi.asset.mapper.PurchaseMapper;
import com.ruoyi.asset.service.IPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资产采购Service业务层处理
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Service
public class PurchaseServiceImpl extends ServiceImpl<PurchaseMapper, Purchase> implements IPurchaseService {

    @Autowired
    private PurchaseMapper purchaseMapper;

    /**
     * 查询资产采购列表
     */
    @Override
    public List<Purchase> selectPurchaseList(Purchase purchase) {
        return purchaseMapper.selectPurchaseList(purchase);
    }
}
