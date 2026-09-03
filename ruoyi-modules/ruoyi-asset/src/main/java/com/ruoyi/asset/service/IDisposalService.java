package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Disposal;

import java.util.List;
import java.util.Map;

/**
 * 资产处置 服务层接口。
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface IDisposalService extends IService<Disposal> {
    /**
     * 查询资产处置列表
     *
     * @param disposal 资产处置查询条件
     * @return 资产处置集合
     */
    List<Disposal> selectDisposalList(Disposal disposal);

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
