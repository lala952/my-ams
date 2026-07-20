package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Transfer;

import java.util.List;
import java.util.Map;

public interface ITransferService extends IService<Transfer> {
    List<Transfer> selectTransferList(Transfer transfer);

    boolean batchApprove(Map<String, Object> params);

    Map<String, Integer> countByStatus();
}
