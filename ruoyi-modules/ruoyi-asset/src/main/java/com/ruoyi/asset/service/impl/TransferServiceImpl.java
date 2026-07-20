package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Transfer;
import com.ruoyi.asset.mapper.TransferMapper;
import com.ruoyi.asset.service.ITransferService;
import com.ruoyi.workflow.api.constant.BusinessStatusConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TransferServiceImpl extends ServiceImpl<TransferMapper, Transfer> implements ITransferService {
    @Autowired
    private TransferMapper transferMapper;

    @Override
    public List<Transfer> selectTransferList(Transfer transfer) {
        LambdaQueryWrapper<Transfer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(transfer.getTransferCode() != null, Transfer::getTransferCode, transfer.getTransferCode())
                .eq(transfer.getBusinessStatus() != null, Transfer::getBusinessStatus, transfer.getBusinessStatus())
                .orderByDesc(Transfer::getCreateTime);
        return transferMapper.selectList(wrapper);
    }

    @Override
    public boolean batchApprove(Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        String remark = (String) params.get("remark");
        String result = (String) params.get("result");
        if (ids == null || ids.isEmpty()) return false;
        for (Long id : ids) {
            Transfer t = this.getById(id);
            if (t != null && BusinessStatusConstants.PENDING.equals(t.getBusinessStatus())) {
                t.setBusinessStatus("approved".equals(result) ? BusinessStatusConstants.COMPLETED : BusinessStatusConstants.REJECTED);
                t.setRemark(remark);
                this.updateById(t);
            }
        }
        return true;
    }

    @Override
    public Map<String, Integer> countByStatus() {
        return transferMapper.countByStatus();
    }
}
