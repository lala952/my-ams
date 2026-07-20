package com.ruoyi.asset.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.mapper.SplitMapper;
import com.ruoyi.asset.domain.Split;
import com.ruoyi.asset.service.ISplitService;

@Service
public class SplitServiceImpl extends ServiceImpl<SplitMapper, Split> implements ISplitService {
    @Autowired
    private SplitMapper splitMapper;

    @Override
    public List<Split> selectSplitList(Split split) {
        return splitMapper.selectSplitList(split);
    }
}