package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Disposal;

import java.util.List;
import java.util.Map;

public interface IDisposalService extends IService<Disposal> {
    List<Disposal> selectDisposalList(Disposal disposal);

    boolean batchApprove(Map<String, Object> params);

    Map<String, Integer> countByStatus();
}
