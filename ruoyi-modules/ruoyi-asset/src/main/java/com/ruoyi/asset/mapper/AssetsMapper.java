package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Assets;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资产台账Mapper接口
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public interface AssetsMapper extends BaseMapper<Assets> {
    /**
     * 查询资产台账列表
     *
     * @param assets 资产台账
     * @return 资产台账集合
     */
    List<Assets> selectAssetsList(Assets assets);

    /**
     * 根据主键数组批量查询资产台账
     *
     * @param ids 资产主键数组
     * @return 资产台账集合
     */
    List<Assets> selectAssetsByIds(Long[] ids);

    /**
     * 根据资产编码查询
     *
     * @param assetCode 资产编码
     * @return 资产台账
     */
    Assets selectByAssetCode(@Param("assetCode") String assetCode);
}