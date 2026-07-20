package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Depreciation;
import com.ruoyi.asset.mapper.DepreciationMapper;
import com.ruoyi.asset.service.IDepreciationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资产折旧Service业务层处理
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Service
public class DepreciationServiceImpl extends ServiceImpl<DepreciationMapper, Depreciation> implements IDepreciationService {

    @Autowired
    private DepreciationMapper depreciationMapper;

    /**
     * 查询资产折旧列表
     */
    @Override
    public List<Depreciation> selectDepreciationList(Depreciation depreciation) {
        return depreciationMapper.selectDepreciationList(depreciation);
    }
}
