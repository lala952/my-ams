package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 通用资产附件对象 asset_attachment（支持多业务模块复用）
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_attachment")
public class Attachment {
    private static final long serialVersionUID = 1L;

    /**
     * 附件ID
     */
    private Long id;

    /**
     * 关联主表ID（逻辑外键）
     */
    private Long masterId;

    /**
     * 附件名称
     */
    private String attachmentName;

    /**
     * 附件类型
     */
    private String attachmentType;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 是否主图
     */
    private Long isMain;

    /**
     * 排序
     */
    private Long sortOrder;

    /**
     * 上传人
     */
    private String uploadBy;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
