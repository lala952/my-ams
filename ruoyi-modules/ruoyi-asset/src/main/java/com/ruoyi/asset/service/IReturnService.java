package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Return;

import java.util.List;
import java.util.Map;

/**
 * 资产交回Service接口
 *
 * @author ruoyi
 */
public interface IReturnService extends IService<Return> {

    List<Return> selectReturnList(Return aReturn);

    boolean batchApprove(Map<String, Object> params);

    Map<String, Integer> countByStatus();
}
