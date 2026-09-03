package com.ruoyi.asset.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.SplitResult;

/**
 * 资产拆分结果 服务层接口。
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface ISplitResultService extends IService<SplitResult> {
    /**
     * 查询资产拆分结果列表
     *
     * @param splitResult 资产拆分结果
     * @return 资产拆分结果集合
     */
    public List<SplitResult> selectSplitResultList(SplitResult splitResult);
}