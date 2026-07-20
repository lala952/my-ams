package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Assets;

import java.util.List;

/**
 * 资产台账Service接口
 *
 * @author xiaowang
 * @date 2026-03-30
 */
public interface IAssetsService extends IService<Assets> {


    /**
     * 根据ID查询资产
     */
    Assets selectAssetsById(Long id);

    /**
     * 查询资产列表
     */
    List<Assets> selectAssetsList(Assets assets);

    /**
     * 根据ID数组批量查询资产
     */
    List<Assets> selectAssetsByIds(Long[] ids);

    /**
     * 根据资产编码查询
     */
    Assets selectByAssetCode(String assetCode);

    /**
     * 根据id查询资产
     */
    public Assets queryById(Long id);

    boolean updateById(Assets assets);
    boolean removeByIds(List<Long> ids);

}