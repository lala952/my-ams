package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Receive;

import java.util.List;
import java.util.Map;

/**
 * 资产领用Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface ReceiveMapper extends BaseMapper<Receive> {

    /**
     * 查询资产领用列表
     *
     * @param receive 资产领用
     * @return 资产领用集合
     */
    List<Receive> selectReceiveList(Receive receive);
}
