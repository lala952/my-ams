package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.History;

import java.util.List;
import java.util.Map;

public interface HistoryMapper extends BaseMapper<History> {
    public List<History> selectHistoryList(History history);

    int insertHistory(Map<String, Object> history);
}
