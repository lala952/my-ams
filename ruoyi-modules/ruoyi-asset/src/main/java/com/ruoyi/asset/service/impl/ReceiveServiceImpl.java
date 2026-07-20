package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Receive;
import com.ruoyi.asset.mapper.ReceiveMapper;
import com.ruoyi.asset.service.IReceiveService;
import com.ruoyi.workflow.api.constant.BusinessStatusConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 资产领用Service业务层处理
 *
 * @author xiaowang
 * @date 2026-04-01
 */
@Service
public class ReceiveServiceImpl extends ServiceImpl<ReceiveMapper, Receive> implements IReceiveService {

    @Autowired
    private ReceiveMapper receiveMapper;

    /**
     * 查询资产领用列表
     */
    @Override
    public List<Receive> selectReceiveList(Receive receive) {
        LambdaQueryWrapper<Receive> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(receive.getReceiveCode() != null, Receive::getReceiveCode, receive.getReceiveCode())
                .eq(receive.getReceivePersonId() != null, Receive::getReceivePersonId, receive.getReceivePersonId())
                .eq(receive.getDeptId() != null, Receive::getDeptId, receive.getDeptId())
                .eq(receive.getBusinessStatus() != null, Receive::getBusinessStatus, receive.getBusinessStatus())
                .orderByDesc(Receive::getCreateTime);
        return receiveMapper.selectList(wrapper);
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
            Receive receive = this.getById(id);
            if (receive != null && BusinessStatusConstants.PENDING.equals(receive.getBusinessStatus())) {
                receive.setBusinessStatus("approved".equals(result) ? BusinessStatusConstants.COMPLETED : BusinessStatusConstants.REJECTED);
                receive.setRemark(remark);
                this.updateById(receive);
            }
        }
        return true;
    }

    /**
     * 统计各状态数量
     */
    @Override
    public Map<String, Integer> countByStatus() {
        return receiveMapper.countByStatus();
    }
}
