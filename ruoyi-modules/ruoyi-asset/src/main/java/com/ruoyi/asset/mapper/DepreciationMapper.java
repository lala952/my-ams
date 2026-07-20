package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Depreciation;

import java.util.List;

public interface DepreciationMapper extends BaseMapper<Depreciation> {
    public List<Depreciation> selectDepreciationList(Depreciation depreciation);
}
