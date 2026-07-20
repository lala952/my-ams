package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.History;
import com.ruoyi.asset.mapper.HistoryMapper;
import com.ruoyi.asset.service.IHistoryService;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.ip.IpUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志Service业务层处理
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History> implements IHistoryService {

    @Autowired
    private HistoryMapper historyMapper;

    /**
     * 查询操作日志列表
     */
    @Override
    public List<History> selectHistoryList(History history) {
        return historyMapper.selectHistoryList(history);
    }

    /**
     * 新增操作日志
     */
    @Override
    public boolean save(History history) {
        history.setOperatorId(SecurityUtils.getUserId());
        history.setOperatorName(SecurityUtils.getUsername());
        history.setOperateTime(DateUtils.getNowDate());
        history.setIpAddress(IpUtils.getIpAddr());
        return super.save(history);
    }

    /**
     * 修改操作日志
     */
    @Override
    public boolean updateById(History history) {
        history.setOperatorId(SecurityUtils.getUserId());
        history.setOperatorName(SecurityUtils.getUsername());
        history.setOperateTime(DateUtils.getNowDate());
        history.setIpAddress(IpUtils.getIpAddr());
        return super.updateById(history);
    }
}
