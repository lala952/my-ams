package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Maintain;

import java.util.List;
import java.util.Map;

/**
 * 资产维修Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface MaintainMapper extends BaseMapper<Maintain> {
    /**
     * 查询资产维修列表
     *
     * @param maintain 资产维修
     * @return 资产维修集合
     */
    List<Maintain> selectMaintainList(Maintain maintain);

    /**
     * 统计各状态数量
     *
     * @return 状态统计结果
     */
    Map<String, Integer> countByStatus();
}
