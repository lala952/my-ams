package com.ruoyi.asset.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.asset.domain.Attachment;
import com.ruoyi.asset.mapper.AttachmentMapper;
import com.ruoyi.asset.service.IAttachmentService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 附件管理Service业务层处理
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Service
public class AttachmentServiceImpl extends ServiceImpl<AttachmentMapper, Attachment> implements IAttachmentService {

    /**
     * 查询附件
     */
    @Override
    public Attachment selectAttachmentById(Long id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询附件列表
     */
    @Override
    public List<Attachment> selectAttachmentList(Attachment attachment) {
        return baseMapper.selectAttachmentList(attachment);
    }

    /**
     * 批量查询附件
     */
    @Override
    public List<Attachment> selectAttachmentByIds(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return Arrays.asList();
        }
        List<Long> idList = Arrays.asList(ids);
        return baseMapper.selectAttachmentByIds(idList);
    }
}