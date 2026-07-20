package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Plan;
import com.ruoyi.asset.domain.PlanDetail;
import com.ruoyi.asset.mapper.PlanMapper;
import com.ruoyi.asset.service.IPlanService;
import com.ruoyi.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 资产配置计划Service业务层处理
 *
 * @author xiaowang
 * @date 2026-05-07
 */
@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements IPlanService {

    @Autowired
    private PlanMapper planMapper;

    /**
     * 查询资产配置计划列表
     */
    @Override
    public List<Plan> selectPlanList(Plan plan) {
        List<Plan> list = planMapper.selectPlanList(plan);
        // 加载明细数据
        for (Plan p : list) {
            List<PlanDetail> details = planMapper.selectDetailListByMasterId(p.getId());
            p.setDetails(details);
        }
        return list;
    }

    /**
     * 根据ID数组批量查询计划
     */
    @Override
    public List<Plan> selectPlanByIds(Long[] ids) {
        List<Plan> list = planMapper.selectPlanByIds(ids);
        // 加载明细数据
        for (Plan p : list) {
            List<PlanDetail> details = planMapper.selectDetailListByMasterId(p.getId());
            p.setDetails(details);
        }
        return list;
    }

    /**
     * 新增配置计划（包含明细）
     */
    @Override
    @Transactional
    public boolean save(Plan plan) {
        // 自动生成计划编号
        if (StringUtils.isEmpty(plan.getPlanCode())) {
            plan.setPlanCode(generatePlanCode());
        }

        boolean result = super.save(plan);

        // 保存明细
        if (result && plan.getDetails() != null && !plan.getDetails().isEmpty()) {
            for (PlanDetail detail : plan.getDetails()) {
                detail.setMasterId(plan.getId());
                // 计算总价
                if (detail.getQuantity() != null && detail.getEstimatedPrice() != null) {
                    detail.setTotalPrice(detail.getEstimatedPrice().multiply(new BigDecimal(detail.getQuantity())));
                }
            }
            planMapper.batchInsertDetail(plan.getDetails());
        }

        return result;
    }

    /**
     * 修改配置计划（包含明细）
     */
    @Override
    @Transactional
    public boolean updateById(Plan plan) {
        boolean result = super.updateById(plan);

        // 更新明细：先删除旧明细，再插入新明细
        if (result && plan.getDetails() != null) {
            planMapper.deleteDetailByMasterId(plan.getId());

            if (!plan.getDetails().isEmpty()) {
                for (PlanDetail detail : plan.getDetails()) {
                    detail.setMasterId(plan.getId());
                    // 计算总价
                    if (detail.getQuantity() != null && detail.getEstimatedPrice() != null) {
                        detail.setTotalPrice(detail.getEstimatedPrice().multiply(new BigDecimal(detail.getQuantity())));
                    }
                }
                planMapper.batchInsertDetail(plan.getDetails());
            }
        }

        return result;
    }

    /**
     * 提交审批
     */
    @Override
    @Transactional
    public boolean submitPlan(Long id) {
        Plan plan = getById(id);
        if (plan == null) {
            return false;
        }
        // 只有草稿状态才能提交
        if (!"draft".equals(plan.getBusinessStatus())) {
            return false;
        }
        plan.setBusinessStatus("submitted");
        return updateById(plan);
    }

    /**
     * 生成采购单
     */
    @Override
    public boolean generatePurchase(Long id) {
        Plan plan = getById(id);
        if (plan == null) {
            return false;
        }
        // 只有已批准的计划才能生成采购单
        if (!"approved".equals(plan.getBusinessStatus())) {
            return false;
        }
        // TODO: 实现生成采购单的逻辑，目前仅返回true
        return true;
    }

    /**
     * 生成计划编号
     */
    private String generatePlanCode() {
        // 格式：JH + 年月日 + 4位流水号
        String dateStr = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        String prefix = "JH" + dateStr;
        // 简化实现：使用时间戳后4位
        String seq = String.format("%04d", System.currentTimeMillis() % 10000);
        return prefix + seq;
    }
}
