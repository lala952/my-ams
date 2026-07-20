package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Scrap;
import com.ruoyi.asset.mapper.ScrapMapper;
import com.ruoyi.asset.service.IScrapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScrapServiceImpl extends ServiceImpl<ScrapMapper, Scrap> implements IScrapService {
    @Autowired
    private ScrapMapper scrapMapper;

    @Override
    public List<Scrap> selectScrapList(Scrap scrap) {
        return scrapMapper.selectScrapList(scrap);
    }

    @Override
    public boolean save(Scrap scrap) {
        // 审计字段由 MyBatis-Plus 自动填充，无需手动设置
        return super.save(scrap);
    }

    @Override
    public boolean updateById(Scrap scrap) {
        // 审计字段由 MyBatis-Plus 自动填充，无需手动设置
        return super.updateById(scrap);
    }
}
