package com.ruoyi.asset.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Split;

/**
 * 资产拆分 服务层接口。
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface ISplitService extends IService<Split> {
    /**
     * 查询资产拆分明细列表
     *
     * @param split 资产拆分明细
     * @return 资产拆分明细集合
     */
    public List<Split> selectSplitList(Split split);
}