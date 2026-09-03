package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Maintain;

import java.util.List;
import java.util.Map;

/**
 * 资产维修 服务层接口。
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface IMaintainService extends IService<Maintain> {
    /**
     * 查询资产维修列表
     *
     * @param maintain 资产维修查询条件
     * @return 资产维修集合
     */
    List<Maintain> selectMaintainList(Maintain maintain);

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
