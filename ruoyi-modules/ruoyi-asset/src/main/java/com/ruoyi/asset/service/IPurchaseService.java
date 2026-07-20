package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Purchase;

import java.util.List;

/**
 * 资产申购Service接口
 *
 * @author xiaowang
 */
public interface IPurchaseService extends IService<Purchase> {


    /**
     * 查询申购单列表
     */
    List<Purchase> selectPurchaseList(Purchase purchase);
}