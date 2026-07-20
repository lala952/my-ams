package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Handover;

import java.util.List;
import java.util.Map;

public interface IHandoverService extends IService<Handover> {
    List<Handover> selectHandoverList(Handover handover);

    boolean batchApprove(Map<String, Object> params);

    Map<String, Integer> countByStatus();
}
