package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Return;
import com.ruoyi.asset.mapper.ReturnMapper;
import com.ruoyi.asset.service.IReturnService;
import com.ruoyi.workflow.api.constant.BusinessStatusConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 资产归还Service业务层处理
 *
 * @author xiaowang
 * @date 2026-04-01
 */
@Service
public class ReturnServiceImpl extends ServiceImpl<ReturnMapper, Return> implements IReturnService {

    @Autowired
    private ReturnMapper returnMapper;

    /**
     * 查询资产归还列表
     */
    @Override
    public List<Return> selectReturnList(Return aReturn) {
        LambdaQueryWrapper<Return> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(aReturn.getReturnCode() != null, Return::getReturnCode, aReturn.getReturnCode())
                .eq(aReturn.getBusinessStatus() != null, Return::getBusinessStatus, aReturn.getBusinessStatus())
                .orderByDesc(Return::getCreateTime);
        return returnMapper.selectList(wrapper);
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
            Return aReturn = this.getById(id);
            if (aReturn != null && BusinessStatusConstants.PENDING.equals(aReturn.getBusinessStatus())) {
                aReturn.setBusinessStatus("approved".equals(result) ? BusinessStatusConstants.COMPLETED : BusinessStatusConstants.REJECTED);
                aReturn.setRemark(remark);
                this.updateById(aReturn);
            }
        }
        return true;
    }

    /**
     * 统计各状态数量
     */
    @Override
    public Map<String, Integer> countByStatus() {
        return returnMapper.countByStatus();
    }
}
