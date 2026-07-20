package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Assets;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssetsMapper extends BaseMapper<Assets> {
    // 查询接口
    List<Assets> selectAssetsList(Assets assets);

    List<Assets> selectAssetsByIds(Long[] ids);

    /**
     * 根据资产编码查询
     */
    Assets selectByAssetCode(@Param("assetCode") String assetCode);
}