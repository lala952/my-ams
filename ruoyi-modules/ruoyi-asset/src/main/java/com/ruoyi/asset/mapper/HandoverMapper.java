package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Handover;

import java.util.List;
import java.util.Map;

public interface HandoverMapper extends BaseMapper<Handover> {
    List<Handover> selectHandoverList(Handover handover);

    Map<String, Integer> countByStatus();
}
