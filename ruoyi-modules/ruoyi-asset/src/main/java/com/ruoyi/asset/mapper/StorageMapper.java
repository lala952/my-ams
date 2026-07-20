package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Storage;

import java.util.List;
import java.util.Map;

/**
 * 资产入库Mapper接口
 *
 * @author ruoyi
 */
public interface StorageMapper extends BaseMapper<Storage> {

    /**
     * 查询资产入库列表
     *
     * @param storage 资产入库
     * @return 资产入库集合
     */
    List<Storage> selectStorageList(Storage storage);

    /**
     * 统计各状态数量
     *
     * @return 状态统计结果
     */
    Map<String, Integer> countByStatus();
}
