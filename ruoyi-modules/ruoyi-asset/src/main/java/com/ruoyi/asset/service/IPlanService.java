package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Plan;

import java.util.List;

/**
 * 资产配置计划Service接口
 *
 * @author xiaowang
 * @date 2026-05-07
 */
public interface IPlanService extends IService<Plan> {
    /**
     * 查询资产配置计划列表
     */
    List<Plan> selectPlanList(Plan plan);

    /**
     * 根据ID数组批量查询计划
     */
    List<Plan> selectPlanByIds(Long[] ids);

    /**
     * 提交审批
     */
    boolean submitPlan(Long id);

    /**
     * 生成采购单
     */
    boolean generatePurchase(Long id);
}
