package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Receive;

import java.util.List;
import java.util.Map;

/**
 * 资产领用Service接口
 *
 * @author ruoyi
 */
public interface IReceiveService extends IService<Receive> {

    /**
     * 查询资产领用列表
     *
     * @param receive 资产领用
     * @return 资产领用集合
     */
    List<Receive> selectReceiveList(Receive receive);

    /**
     * 批量审批
     *
     * @param params 审批参数
     * @return 结果
     */
    boolean batchApprove(Map<String, Object> params);

    /**
     * 统计各状态数量
     *
     * @return 状态统计结果
     */
    Map<String, Integer> countByStatus();
}
