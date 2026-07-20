package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Scrap;

import java.util.List;

public interface ScrapMapper extends BaseMapper<Scrap> {
    public List<Scrap> selectScrapList(Scrap scrap);
}
