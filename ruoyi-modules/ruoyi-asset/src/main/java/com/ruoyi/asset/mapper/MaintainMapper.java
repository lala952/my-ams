package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Maintain;

import java.util.List;
import java.util.Map;

public interface MaintainMapper extends BaseMapper<Maintain> {
    List<Maintain> selectMaintainList(Maintain maintain);

    Map<String, Integer> countByStatus();
}
