package com.ruoyi.asset.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.asset.domain.Attachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AttachmentMapper extends BaseMapper<Attachment> {
    // 查询接口（复杂查询保留）
    List<Attachment> selectAttachmentList(Attachment attachment);

    List<Attachment> selectAttachmentByIds(@Param("ids") List<Long> ids);
}