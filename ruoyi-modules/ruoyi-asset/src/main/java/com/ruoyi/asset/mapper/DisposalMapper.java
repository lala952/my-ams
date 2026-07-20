package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Disposal;

import java.util.List;
import java.util.Map;

public interface DisposalMapper extends BaseMapper<Disposal> {
    List<Disposal> selectDisposalList(Disposal disposal);

    Map<String, Integer> countByStatus();
}
