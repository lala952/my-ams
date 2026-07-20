package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Depreciation;

import java.util.List;

/**
 * 资产折旧记录Service接口
 *
 * @author xiaowang
 */
public interface IDepreciationService extends IService<Depreciation> {


    /**
     * 查询折旧记录列表
     */
    List<Depreciation> selectDepreciationList(Depreciation depreciation);
}