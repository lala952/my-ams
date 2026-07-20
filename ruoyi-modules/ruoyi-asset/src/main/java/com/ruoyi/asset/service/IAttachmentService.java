package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.Attachment;

import java.util.List;

/**
 * 资产附件Service接口
 *
 * @author xiaowang
 */
public interface IAttachmentService extends IService<Attachment> {


    /**
     * 根据ID查询附件
     */
    Attachment selectAttachmentById(Long id);

    /**
     * 查询附件列表
     */
    List<Attachment> selectAttachmentList(Attachment attachment);

    /**
     * 根据ID数组批量查询附件
     */
    List<Attachment> selectAttachmentByIds(Long[] ids);
}