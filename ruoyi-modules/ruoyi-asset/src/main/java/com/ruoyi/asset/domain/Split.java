package com.ruoyi.asset.domain;

    import java.util.Date;
    import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 资产拆分明细对象 asset_split
 *
 * @author xiaowang
 * @date 2026-06-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
    @TableName("asset_split" )
public class Split extends BaseEntity  {
private static final long serialVersionUID=1L;

    /** 拆分ID */
        @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    /** 拆分单编码 */
        @TableField("split_code" )
    private String splitCode;

    /** 原资产ID */
        @TableField("source_asset_id" )
    private Long sourceAssetId;

    /** 拆分原因 */
        @TableField("split_reason" )
    private String splitReason;

    /** 拆分日期 */
        @TableField("split_date" )
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date splitDate;

    /** 拆分总数（被拆出的数量） */
        @TableField("split_quantity" )
    private Long splitQuantity;

    /** 业务状态：draft-草稿，pending-办理中，rejected-已退回，completed-已完成 */
        @TableField("business_status" )
    private String businessStatus;

    /** 审批人ID */
        @TableField("approver_id" )
    private Long approverId;

    /** 审批时间 */
        @TableField("approve_time" )
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date approveTime;

    /** Activiti流程实例ID */
        @TableField("proc_inst_id" )
    private String procInstId;

    /** 删除标志（0代表存在 2代表删除） */
        @TableField("del_flag" )
    private String delFlag;

        }