package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Return;

import java.util.List;
import java.util.Map;

public interface ReturnMapper extends BaseMapper<Return> {

    List<Return> selectReturnList(Return aReturn);

    Map<String, Integer> countByStatus();
}
