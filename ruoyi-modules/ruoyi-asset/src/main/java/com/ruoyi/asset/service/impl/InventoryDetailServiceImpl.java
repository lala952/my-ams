package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.InventoryDetail;
import com.ruoyi.asset.mapper.InventoryDetailMapper;
import com.ruoyi.asset.service.IInventoryDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 盘点明细Service业务层处理
 *
 * @author xiaowang
 * @date 2026-04-01
 */
@Service
public class InventoryDetailServiceImpl extends ServiceImpl<InventoryDetailMapper, InventoryDetail> implements IInventoryDetailService {

    @Autowired
    private InventoryDetailMapper inventoryDetailMapper;

    /**
     * 查询盘点明细列表
     */
    @Override
    public List<InventoryDetail> selectInventoryDetailList(InventoryDetail inventoryDetail) {
        return inventoryDetailMapper.selectInventoryDetailList(inventoryDetail);
    }
}
