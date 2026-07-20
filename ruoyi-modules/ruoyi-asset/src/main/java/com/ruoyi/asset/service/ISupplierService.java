package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Supplier;

import java.util.List;

/**
 * 供应商Service接口
 *
 * @author xiaowang
 */
public interface ISupplierService extends IService<Supplier> {


    /**
     * 查询供应商列表
     */
    List<Supplier> selectSupplierList(Supplier supplier);
}