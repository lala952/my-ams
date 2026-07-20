package com.ruoyi.asset.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.SplitResult;

public interface ISplitResultService extends IService<SplitResult> {
    /**
     * 查询资产拆分结果列表
     *
     * @param splitResult 资产拆分结果
     * @return 资产拆分结果集合
     */
    public List<SplitResult> selectSplitResultList(SplitResult splitResult);
}