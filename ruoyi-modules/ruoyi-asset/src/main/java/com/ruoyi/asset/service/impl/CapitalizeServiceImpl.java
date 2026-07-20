package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Capitalize;
import com.ruoyi.asset.mapper.CapitalizeMapper;
import com.ruoyi.asset.service.ICapitalizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资产转固Service业务层处理
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Service
public class CapitalizeServiceImpl extends ServiceImpl<CapitalizeMapper, Capitalize> implements ICapitalizeService {

    @Autowired
    private CapitalizeMapper capitalizeMapper;

    /**
     * 查询资产转固列表
     */
    @Override
    public List<Capitalize> selectCapitalizeList(Capitalize capitalize) {
        return capitalizeMapper.selectCapitalizeList(capitalize);
    }
}
