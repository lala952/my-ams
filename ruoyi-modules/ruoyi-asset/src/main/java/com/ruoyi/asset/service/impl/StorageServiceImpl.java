package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Storage;
import com.ruoyi.asset.mapper.StorageMapper;
import com.ruoyi.asset.service.IStorageService;
import com.ruoyi.workflow.api.constant.BusinessStatusConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 资产入库Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements IStorageService {

    @Autowired
    private StorageMapper storageMapper;

    /**
     * 查询资产入库列表
     *
     * @param storage 资产入库
     * @return 资产入库
     */
    @Override
    public List<Storage> selectStorageList(Storage storage) {
        LambdaQueryWrapper<Storage> wrapper = new LambdaQueryWrapper<>();

        // 根据条件查询
        if (storage.getStorageCode() != null && !storage.getStorageCode().isEmpty()) {
            wrapper.like(Storage::getStorageCode, storage.getStorageCode());
        }
        if (storage.getBusinessStatus() != null && !storage.getBusinessStatus().isEmpty()) {
            wrapper.eq(Storage::getBusinessStatus, storage.getBusinessStatus());
        }

        wrapper.orderByDesc(Storage::getCreateTime);

        return storageMapper.selectList(wrapper);
    }

    /**
     * 批量审批
     *
     * @param params 审批参数
     * @return 结果
     */
    @Override
    public boolean batchApprove(Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) params.get("ids");
        String result = (String) params.get("result");
        String remark = (String) params.get("remark");

        if (ids == null || ids.isEmpty()) {
            return false;
        }

        // 更新审批状态
        for (Long id : ids) {
            Storage storage = this.getById(id);
            if (storage != null) {
                storage.setBusinessStatus(BusinessStatusConstants.COMPLETED);
                storage.setRemark(remark);
                this.updateById(storage);
            }
        }

        return true;
    }

    /**
     * 统计各状态数量
     *
     * @return 状态统计结果
     */
    @Override
    public Map<String, Integer> countByStatus() {
        return storageMapper.countByStatus();
    }
}
