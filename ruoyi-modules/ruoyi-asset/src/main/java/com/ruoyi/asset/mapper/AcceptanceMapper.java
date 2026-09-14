package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Acceptance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资产验收Mapper接口
 *
 * @author xiaowang
 * @date 2026-03-30
 */
public interface AcceptanceMapper extends BaseMapper<Acceptance> {
    /**
     * 查询资产验收列表（复杂查询）
     *
     * @param acceptance 资产验收
     * @return 资产验收集合
     */
    public List<Acceptance> selectAcceptanceList(Acceptance acceptance);
}