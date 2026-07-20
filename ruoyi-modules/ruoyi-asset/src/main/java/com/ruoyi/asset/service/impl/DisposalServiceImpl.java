package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Disposal;
import com.ruoyi.asset.mapper.DisposalMapper;
import com.ruoyi.asset.service.IDisposalService;
import com.ruoyi.workflow.api.constant.BusinessStatusConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 资产处置Service业务层处理
 *
 * @author xiaowang
 * @date 2026-04-01
 */
@Service
public class DisposalServiceImpl extends ServiceImpl<DisposalMapper, Disposal> implements IDisposalService {

    @Autowired
    private DisposalMapper disposalMapper;

    /**
     * 查询资产处置列表
     */
    @Override
    public List<Disposal> selectDisposalList(Disposal disposal) {
        LambdaQueryWrapper<Disposal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(disposal.getDisposalCode() != null, Disposal::getDisposalCode, disposal.getDisposalCode())
                .eq(disposal.getBusinessStatus() != null, Disposal::getBusinessStatus, disposal.getBusinessStatus())
                .orderByDesc(Disposal::getCreateTime);
        return disposalMapper.selectList(wrapper);
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
            Disposal d = this.getById(id);
            if (d != null && BusinessStatusConstants.PENDING.equals(d.getBusinessStatus())) {
                d.setBusinessStatus("approved".equals(result) ? BusinessStatusConstants.COMPLETED : BusinessStatusConstants.REJECTED);
                d.setRemark(remark);
                this.updateById(d);
            }
        }
        return true;
    }

    /**
     * 按状态统计数量
     */
    @Override
    public Map<String, Integer> countByStatus() {
        return disposalMapper.countByStatus();
    }
}
