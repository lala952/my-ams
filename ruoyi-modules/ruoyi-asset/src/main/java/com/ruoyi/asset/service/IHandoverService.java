package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Handover;

import java.util.List;
import java.util.Map;

/**
 * 资产移交 服务层接口。
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface IHandoverService extends IService<Handover> {
    /**
     * 查询资产移交列表
     *
     * @param handover 资产移交查询条件
     * @return 资产移交集合
     */
    List<Handover> selectHandoverList(Handover handover);

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
