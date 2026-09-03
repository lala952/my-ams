package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Capitalize;

import java.util.List;

/**
 * 资产转资Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface CapitalizeMapper extends BaseMapper<Capitalize> {
    /**
     * 查询资产转资列表
     *
     * @param capitalize 资产转资
     * @return 资产转资集合
     */
    public List<Capitalize> selectCapitalizeList(Capitalize capitalize);
}
