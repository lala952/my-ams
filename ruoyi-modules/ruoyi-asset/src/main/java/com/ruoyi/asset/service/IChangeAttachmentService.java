package com.ruoyi.asset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.asset.domain.ChangeAttachment;

import java.util.List;

/**
 * 资产变动附件Service接口
 *
 * @author xiaowang
 * @date 2026-05-11
 */
public interface IChangeAttachmentService extends IService<ChangeAttachment> {
    /**
     * 查询资产变动附件列表
     *
     * @param changeAttachment 资产变动附件
     * @return 资产变动附件集合
     */
    List<ChangeAttachment> selectChangeAttachmentList(ChangeAttachment changeAttachment);

    /**
     * 根据变动单ID删除附件
     *
     * @param masterId 变动单ID
     * @return 结果
     */
    int deleteByMasterId(Long masterId);
}
