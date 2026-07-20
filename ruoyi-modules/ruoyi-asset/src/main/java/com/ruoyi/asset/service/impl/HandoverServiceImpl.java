package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Handover;
import com.ruoyi.asset.mapper.HandoverMapper;
import com.ruoyi.asset.service.IHandoverService;
import com.ruoyi.workflow.api.constant.BusinessStatusConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 资产交接Service业务层处理
 *
 * @author xiaowang
 * @date 2026-04-01
 */
@Service
public class HandoverServiceImpl extends ServiceImpl<HandoverMapper, Handover> implements IHandoverService {

    @Autowired
    private HandoverMapper handoverMapper;

    /**
     * 查询资产交接列表
     */
    @Override
    public List<Handover> selectHandoverList(Handover handover) {
        LambdaQueryWrapper<Handover> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(handover.getHandoverCode() != null, Handover::getHandoverCode, handover.getHandoverCode())
                .eq(handover.getBusinessStatus() != null, Handover::getBusinessStatus, handover.getBusinessStatus())
                .orderByDesc(Handover::getCreateTime);
        return handoverMapper.selectList(wrapper);
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
            Handover h = this.getById(id);
            if (h != null && BusinessStatusConstants.PENDING.equals(h.getBusinessStatus())) {
                h.setBusinessStatus("approved".equals(result) ? BusinessStatusConstants.COMPLETED : BusinessStatusConstants.REJECTED);
                h.setRemark(remark);
                this.updateById(h);
            }
        }
        return true;
    }

    /**
     * 按状态统计数量
     */
    @Override
    public Map<String, Integer> countByStatus() {
        return handoverMapper.countByStatus();
    }
}
