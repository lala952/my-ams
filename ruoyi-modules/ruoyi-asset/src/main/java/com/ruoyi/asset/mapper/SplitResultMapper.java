package com.ruoyi.asset.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.SplitResult;

public interface SplitResultMapper extends BaseMapper<SplitResult> {
    /**
     * 查询资产拆分结果列表
     *
     * @param splitResult 资产拆分结果
     * @return 资产拆分结果集合
     */
    public List<SplitResult> selectSplitResultList(SplitResult splitResult);
}