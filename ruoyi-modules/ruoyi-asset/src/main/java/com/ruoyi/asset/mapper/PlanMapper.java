package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Plan;
import com.ruoyi.asset.domain.PlanDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资产配置计划Mapper接口
 *
 * @author xiaowang
 * @date 2026-05-07
 */
public interface PlanMapper extends BaseMapper<Plan> {
    /**
     * 查询资产配置计划列表
     *
     * @param plan 资产配置计划
     * @return 资产配置计划集合
     */
    List<Plan> selectPlanList(Plan plan);

    /**
     * 根据ID数组批量查询计划
     *
     * @param ids 计划主键数组
     * @return 资产配置计划集合
     */
    List<Plan> selectPlanByIds(@Param("ids") Long[] ids);

    /**
     * 根据主表ID查询明细列表
     *
     * @param masterId 计划主表ID
     * @return 计划明细集合
     */
    List<PlanDetail> selectDetailListByMasterId(@Param("masterId") Long masterId);

    /**
     * 批量插入明细
     *
     * @param details 计划明细集合
     * @return 结果
     */
    int batchInsertDetail(@Param("details") List<PlanDetail> details);

    /**
     * 删除明细（逻辑删除）
     *
     * @param masterId 计划主表ID
     * @return 结果
     */
    int deleteDetailByMasterId(@Param("masterId") Long masterId);
}
