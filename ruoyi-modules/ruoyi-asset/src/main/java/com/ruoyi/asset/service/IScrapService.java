package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Scrap;

import java.util.List;

/**
 * 资产报废申请Service接口
 *
 * @author xiaowang
 */
public interface IScrapService extends IService<Scrap> {


    /**
     * 查询报废申请列表
     */
    List<Scrap> selectScrapList(Scrap scrap);
}