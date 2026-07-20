package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Capitalize;

import java.util.List;

public interface CapitalizeMapper extends BaseMapper<Capitalize> {
    public List<Capitalize> selectCapitalizeList(Capitalize capitalize);
}
