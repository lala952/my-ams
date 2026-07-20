package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Transfer;

import java.util.List;
import java.util.Map;

public interface TransferMapper extends BaseMapper<Transfer> {
    List<Transfer> selectTransferList(Transfer transfer);

    Map<String, Integer> countByStatus();
}
