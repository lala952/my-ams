package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Scrap;

import java.util.List;

/**
 * 资产报废Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface ScrapMapper extends BaseMapper<Scrap> {
    /**
     * 查询资产报废列表
     *
     * @param scrap 资产报废
     * @return 资产报废集合
     */
    public List<Scrap> selectScrapList(Scrap scrap);
}
