package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Return;

import java.util.List;
import java.util.Map;

/**
 * 资产归还Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface ReturnMapper extends BaseMapper<Return> {

    /**
     * 查询资产归还列表
     *
     * @param aReturn 资产归还
     * @return 资产归还集合
     */
    List<Return> selectReturnList(Return aReturn);

    /**
     * 统计各状态数量
     *
     * @return 状态统计结果
     */
    Map<String, Integer> countByStatus();
}
