package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Disposal;

import java.util.List;
import java.util.Map;

/**
 * 资产处置Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface DisposalMapper extends BaseMapper<Disposal> {
    /**
     * 查询资产处置列表
     *
     * @param disposal 资产处置
     * @return 资产处置集合
     */
    List<Disposal> selectDisposalList(Disposal disposal);

}
