package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Transfer;

import java.util.List;
import java.util.Map;

/**
 * 资产调拨 服务层接口。
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface ITransferService extends IService<Transfer> {
    /**
     * 查询资产调拨列表
     *
     * @param transfer 资产调拨查询条件
     * @return 资产调拨集合
     */
    List<Transfer> selectTransferList(Transfer transfer);

    /**
     * 批量审批
     *
     * @param params 审批参数
     * @return 审批结果
     */
    boolean batchApprove(Map<String, Object> params);

    /**
     * 统计各状态数量
     *
     * @return 状态统计结果
     */
    Map<String, Integer> countByStatus();
}
