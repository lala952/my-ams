package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.ChangeAttachment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 资产变动附件Mapper接口
 *
 * @author xiaowang
 * @date 2026-05-11
 */
@Mapper
public interface ChangeAttachmentMapper extends BaseMapper<ChangeAttachment> {
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
