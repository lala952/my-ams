package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Supplier;

import java.util.List;

public interface SupplierMapper extends BaseMapper<Supplier> {
    public List<Supplier> selectSupplierList(Supplier supplier);
}
