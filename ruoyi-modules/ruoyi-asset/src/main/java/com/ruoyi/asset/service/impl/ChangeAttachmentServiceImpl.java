package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.ChangeAttachment;
import com.ruoyi.asset.mapper.ChangeAttachmentMapper;
import com.ruoyi.asset.service.IChangeAttachmentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资产变动附件Service业务层处理
 *
 * @author xiaowang
 * @date 2026-05-11
 */
@Service
public class ChangeAttachmentServiceImpl extends ServiceImpl<ChangeAttachmentMapper, ChangeAttachment> implements IChangeAttachmentService {
    /**
     * 查询资产变动附件列表
     *
     * @param changeAttachment 资产变动附件
     * @return 资产变动附件集合
     */
    @Override
    public List<ChangeAttachment> selectChangeAttachmentList(ChangeAttachment changeAttachment) {
        LambdaQueryWrapper<ChangeAttachment> wrapper = new LambdaQueryWrapper<>();
        if (changeAttachment.getMasterId() != null) {
            wrapper.eq(ChangeAttachment::getMasterId, changeAttachment.getMasterId());
        }
        if (changeAttachment.getAttachmentType() != null) {
            wrapper.eq(ChangeAttachment::getAttachmentType, changeAttachment.getAttachmentType());
        }
        wrapper.eq(ChangeAttachment::getDelFlag, 0).orderByAsc(ChangeAttachment::getUploadTime);
        return this.list(wrapper);
    }

    /**
     * 根据变动单ID删除附件
     *
     * @param masterId 变动单ID
     * @return 结果
     */
    @Override
    public int deleteByMasterId(Long masterId) {
        LambdaQueryWrapper<ChangeAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChangeAttachment::getMasterId, masterId);
        return this.getBaseMapper().delete(wrapper);
    }
}
