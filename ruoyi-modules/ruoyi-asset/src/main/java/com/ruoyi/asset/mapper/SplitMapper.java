package com.ruoyi.asset.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Split;

public interface SplitMapper extends BaseMapper<Split> {
    /**
     * 查询资产拆分明细列表
     *
     * @param split 资产拆分明细
     * @return 资产拆分明细集合
     */
    public List<Split> selectSplitList(Split split);
}