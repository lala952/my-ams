package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Maintain;
import com.ruoyi.asset.mapper.MaintainMapper;
import com.ruoyi.asset.service.IMaintainService;
import com.ruoyi.workflow.api.constant.BusinessStatusConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 资产维修Service业务层处理
 *
 * @author xiaowang
 * @date 2026-04-01
 */
@Service
public class MaintainServiceImpl extends ServiceImpl<MaintainMapper, Maintain> implements IMaintainService {
    @Autowired
    private MaintainMapper maintainMapper;

    /**
     * 查询资产维修列表
     */
    @Override
    public List<Maintain> selectMaintainList(Maintain maintain) {
        LambdaQueryWrapper<Maintain> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(maintain.getMaintenanceCode() != null, Maintain::getMaintenanceCode, maintain.getMaintenanceCode())
                .eq(maintain.getBusinessStatus() != null, Maintain::getBusinessStatus, maintain.getBusinessStatus())
                .orderByDesc(Maintain::getCreateTime);
        return maintainMapper.selectList(wrapper);
    }

    /**
     * 批量审批
     */
    @Override
    public boolean batchApprove(Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        String remark = (String) params.get("remark");
        String result = (String) params.get("result");
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        for (Long id : ids) {
            Maintain m = this.getById(id);
            if (m != null && BusinessStatusConstants.PENDING.equals(m.getBusinessStatus())) {
                m.setBusinessStatus("approved".equals(result) ? BusinessStatusConstants.COMPLETED : BusinessStatusConstants.REJECTED);
                m.setRemark(remark);
                this.updateById(m);
            }
        }
        return true;
    }

    /**
     * 统计各状态数量
     */
    @Override
    public Map<String, Integer> countByStatus() {
        return maintainMapper.countByStatus();
    }
}
