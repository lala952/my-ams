package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Capitalize;

import java.util.List;

/**
 * 资产转固Service接口
 *
 * @author xiaowang
 */
public interface ICapitalizeService extends IService<Capitalize> {


    /**
     * 查询资产转固列表
     */
    List<Capitalize> selectCapitalizeList(Capitalize capitalize);
}