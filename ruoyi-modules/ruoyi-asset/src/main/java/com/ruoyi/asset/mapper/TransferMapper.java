package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Transfer;

import java.util.List;
import java.util.Map;

/**
 * 资产调拨Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface TransferMapper extends BaseMapper<Transfer> {
    /**
     * 查询资产调拨列表
     *
     * @param transfer 资产调拨
     * @return 资产调拨集合
     */
    List<Transfer> selectTransferList(Transfer transfer);

    /**
     * 统计各状态数量
     *
     * @return 状态统计结果
     */
    Map<String, Integer> countByStatus();
}
