package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 资产变动附件对象 asset_change_attachment
 *
 * @author xiaowang
 * @date 2026-05-11
 */
@Data
@TableName("asset_change_attachment")
public class ChangeAttachment {
    private static final long serialVersionUID = 1L;

    /**
     * 附件ID
     */
    private Long id;

    /**
     * 变动单ID（逻辑外键，关联asset_change.id）
     */
    private Long masterId;

    /**
     * 附件名称
     */
    private String attachmentName;

    /**
     * 附件类型（字典attachment_type）
     */
    private String attachmentType;

    /**
     * 文件路径
     */
    private String filePath;

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
