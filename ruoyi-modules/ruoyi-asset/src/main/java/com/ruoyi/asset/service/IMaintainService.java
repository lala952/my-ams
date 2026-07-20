package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Maintain;

import java.util.List;
import java.util.Map;

public interface IMaintainService extends IService<Maintain> {
    List<Maintain> selectMaintainList(Maintain maintain);

    boolean batchApprove(Map<String, Object> params);

    Map<String, Integer> countByStatus();
}
