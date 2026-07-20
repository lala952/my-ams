package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Acceptance;

import java.util.List;

/**
 * 资产验收Service接口
 *
 * @author xiaowang
 * @date 2026-03-30
 */
public interface IAcceptanceService extends IService<Acceptance> {


    /**
     * 查询资产验收列表
     *
     * @param acceptance 查询条件
     * @return 资产验收集合
     */
    List<Acceptance> selectAcceptanceList(Acceptance acceptance);
}