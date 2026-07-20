package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Assets;
import com.ruoyi.asset.mapper.AssetsMapper;
import com.ruoyi.asset.service.IAssetsService;
import com.ruoyi.asset.utils.CacheClient;
import com.ruoyi.asset.utils.GenerateCode;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.ruoyi.asset.constant.RedisConstants.ASSET_KEY;
import static com.ruoyi.asset.constant.RedisConstants.CACHE_DEFAULT_TTL;

/**
 * 资产台账Service业务层处理
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Service
@Slf4j
public class AssetsServiceImpl extends ServiceImpl<AssetsMapper, Assets> implements IAssetsService {

    @Autowired
    private AssetsMapper assetsMapper;

    @Autowired
    private GenerateCode generateCode;

    @Autowired
    private CacheClient cacheClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 查询资产台账
     */
    @Override
    public Assets selectAssetsById(Long id) {
        return assetsMapper.selectById(id);
    }

    /**
     * 查询资产台账列表
     */
    @Override
    public List<Assets> selectAssetsList(Assets assets) {
        return assetsMapper.selectAssetsList(assets);
    }

    /**
     * 批量查询资产台账
     */
    @Override
    public List<Assets> selectAssetsByIds(Long[] ids) {
        return assetsMapper.selectAssetsByIds(ids);
    }

    /**
     * 根据资产编码查询
     */
    @Override
    public Assets selectByAssetCode(String assetCode) {
        return assetsMapper.selectByAssetCode(assetCode);
    }

    @Override
    public Assets queryById(Long id) {
        return cacheClient.queryWithPassThrough(ASSET_KEY, id, Assets.class, this::getById, CACHE_DEFAULT_TTL, TimeUnit.MINUTES);
    }

    /**
     * 新增资产台账
     */
    @Override
    public boolean save(Assets assets) {
        // 自动生成资产编码
        if (assets.getAssetCode() == null || assets.getAssetCode().isEmpty()) {
            assets.setAssetCode(generateCode.generateCode("ZCTZ"));
        }
        assets.setCreateBy(SecurityUtils.getUsername());
        assets.setCreateTime(DateUtils.getNowDate());
        return super.save(assets);
    }

    /**
     * 修改资产台账
     */
    @Override
    public boolean updateById(Assets assets) {
        boolean result = super.updateById(assets);
        assets.setUpdateBy(SecurityUtils.getUsername());
        assets.setUpdateTime(DateUtils.getNowDate());
        stringRedisTemplate.delete(ASSET_KEY + assets.getId());
        if (result) {
            log.info("资产台账数据已更新，已删除自身缓存和树缓存，id：{}", assets.getId());
        }
        return result;
    }

    @Override
    public boolean removeByIds(List<Long> ids) {
        boolean result = super.removeByIds(ids);
        if (result) {
            ids.forEach(id -> stringRedisTemplate.delete(ASSET_KEY + id));
            log.info("资产数据已删除(logical delete)，已删除自身缓存，ids：{}", ids);
        }
        return result;
    }

}

