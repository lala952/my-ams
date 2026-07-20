package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.History;

import java.util.List;

/**
 * 资产履历Service接口
 *
 * @author xiaowang
 */
public interface IHistoryService extends IService<History> {


    /**
     * 查询履历列表
     */
    List<History> selectHistoryList(History history);
}