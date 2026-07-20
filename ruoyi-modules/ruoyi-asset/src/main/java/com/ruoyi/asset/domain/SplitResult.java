package com.ruoyi.asset.domain;

    import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.EqualsAndHashCode;
import com.ruoyi.common.core.web.domain.BaseEntity;

/**
 * 资产拆分结果对象 asset_split_result
 *
 * @author xiaowang
 * @date 2026-06-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
    @TableName("asset_split_result" )
public class SplitResult extends BaseEntity  {
private static final long serialVersionUID=1L;

    /** 记录ID */
        @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    /** 拆分单ID */
        @TableField("split_id" )
    private Long splitId;

    /** 新资产编码 */
        @TableField("new_asset_code" )
    private String newAssetCode;

    /** 新资产名称 */
        @TableField("new_asset_name" )
    private String newAssetName;

    /** 拆分数量 */
        @TableField("split_quantity" )
    private Long splitQuantity;

    /** 分摊原值 */
        @TableField("original_value" )
    private BigDecimal originalValue;

    /** 使用人ID */
        @TableField("user_id" )
    private Long userId;

    /** 使用部门ID */
        @TableField("dept_id" )
    private Long deptId;

    /** 存放位置 */
        @TableField("location" )
    private String location;

    /** 执行状态：pending-待执行，executing-执行中，success-执行成功，fail-执行失败 */
        @TableField("execute_status" )
    private String executeStatus;

    /** 生成的资产ID */
        @TableField("target_asset_id" )
    private Long targetAssetId;

    /** 删除标志（0代表存在 2代表删除） */
        @TableField("del_flag" )
    private String delFlag;

        }