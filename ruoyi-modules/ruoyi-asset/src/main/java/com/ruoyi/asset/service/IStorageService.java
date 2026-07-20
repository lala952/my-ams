package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Storage;

import java.util.List;
import java.util.Map;

/**
 * 资产入库Service接口
 *
 * @author ruoyi
 */
public interface IStorageService extends IService<Storage> {

    /**
     * 查询资产入库列表
     *
     * @param storage 资产入库
     * @return 资产入库集合
     */
    List<Storage> selectStorageList(Storage storage);

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
