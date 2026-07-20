package com.ruoyi.asset.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.mapper.SplitResultMapper;
import com.ruoyi.asset.domain.SplitResult;
import com.ruoyi.asset.service.ISplitResultService;

@Service
public class SplitResultServiceImpl extends ServiceImpl<SplitResultMapper, SplitResult> implements ISplitResultService {
    @Autowired
    private SplitResultMapper splitResultMapper;

    @Override
    public List<SplitResult> selectSplitResultList(SplitResult splitResult) {
        return splitResultMapper.selectSplitResultList(splitResult);
    }
}