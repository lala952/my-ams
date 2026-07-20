package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Inventory;
import com.ruoyi.asset.domain.InventoryDetail;
import com.ruoyi.asset.mapper.InventoryMapper;
import com.ruoyi.asset.service.IInventoryService;
import com.ruoyi.asset.utils.RedisIdWorker;
import com.ruoyi.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产盘点Service业务层处理
 *
 * @author xiaowang
 * @date 2026-04-01
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {
    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private RedisIdWorker redisIdWorker;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询资产盘点列表
     */
    @Override
    public List<Inventory> selectInventoryList(Inventory inventory) {
        return inventoryMapper.selectInventoryList(inventory);
    }

    /**
     * 新增资产盘点
     */
    @Override
    @Transactional
    public boolean save(Inventory inventory) {
        inventory.setInventoryCode(generateInventoryCode());
        boolean result = super.save(inventory);
        if (result) {
            insertInventoryDetail(inventory);
        }
        return result;
    }

    /**
     * 修改资产盘点
     */
    @Override
    @Transactional
    public boolean updateById(Inventory inventory) {
        inventoryMapper.deleteInventoryDetailByMasterId(inventory.getId());
        boolean result = super.updateById(inventory);
        if (result) {
            insertInventoryDetail(inventory);
        }
        return result;
    }

    /**
     * 删除盘点明细信息
     */
    @Override
    public int deleteInventoryDetailByMasterIds(Long[] ids) {
        return inventoryMapper.deleteInventoryDetailByMasterIds(ids);
    }

    /**
     * 批量新增盘点明细
     */
    @Override
    public int batchInventoryDetail(List<InventoryDetail> inventoryDetailList) {
        return inventoryMapper.batchInventoryDetail(inventoryDetailList);
    }

    /**
     * 删除盘点明细信息
     */
    @Override
    public int deleteInventoryDetailByMasterId(Long id) {
        return inventoryMapper.deleteInventoryDetailByMasterId(id);
    }

    /**
     * 新增资产盘点明细信息
     */
    public void insertInventoryDetail(Inventory inventory) {
        List<InventoryDetail> inventoryDetailList = inventory.getInventoryDetailList();
        Long id = inventory.getId();
        if (StringUtils.isNotNull(inventoryDetailList)) {
            List<InventoryDetail> list = new ArrayList<>();
            for (InventoryDetail inventoryDetail : inventoryDetailList) {
                inventoryDetail.setMasterId(id);
                list.add(inventoryDetail);
            }
            if (list.size() > 0) {
                inventoryMapper.batchInventoryDetail(list);
            }
        }
    }

    /**
     * 生成盘点单号
     * 格式：PD-20260401-00001（前缀 + 日期+5 位序列号）
     *
     * @return 盘点单号
     */
    private String generateInventoryCode() {
        // 获取当前日期，格式：yyyyMMdd
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // 使用 Redis 生成唯一序列号（5 位数字，不足补零）
        long sequence = redisIdWorker.nextId("inventory_code:" + now);
        String sequenceStr = String.format("%05d", sequence % 100000);

        // 组合成完整单号：PD-20260401-00001
        return "PD-" + now + "-" + sequenceStr;
    }
}
