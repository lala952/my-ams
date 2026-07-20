package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Acceptance;
import com.ruoyi.asset.mapper.AcceptanceMapper;
import com.ruoyi.asset.service.IAcceptanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资产验收Service业务层处理
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Service
public class AcceptanceServiceImpl extends ServiceImpl<AcceptanceMapper, Acceptance> implements IAcceptanceService {

    @Autowired
    private AcceptanceMapper acceptanceMapper;

    /**
     * 查询资产验收列表
     */
    @Override
    public List<Acceptance> selectAcceptanceList(Acceptance acceptance) {
        return acceptanceMapper.selectAcceptanceList(acceptance);
    }

    /**
     * 批量删除资产验收
     */
    public boolean removeByIds(List<Long> ids) {
        Long[] idArray = ids.toArray(new Long[0]);
        return acceptanceMapper.deleteAcceptanceByIds(idArray) > 0;
    }
}