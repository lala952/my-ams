package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Handover;

import java.util.List;
import java.util.Map;

/**
 * 资产交接Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface HandoverMapper extends BaseMapper<Handover> {
    /**
     * 查询资产交接列表
     *
     * @param handover 资产交接
     * @return 资产交接集合
     */
    List<Handover> selectHandoverList(Handover handover);

    /**
     * 统计各状态数量
     *
     * @return 状态统计结果
     */
    Map<String, Integer> countByStatus();
}
