package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Depreciation;

import java.util.List;

/**
 * 资产折旧Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface DepreciationMapper extends BaseMapper<Depreciation> {
    /**
     * 查询资产折旧列表
     *
     * @param depreciation 资产折旧
     * @return 资产折旧集合
     */
    public List<Depreciation> selectDepreciationList(Depreciation depreciation);
}
