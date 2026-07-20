package com.ruoyi.asset.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.web.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 资产验收对象 asset_acceptance
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@TableName("asset_acceptance")
@EqualsAndHashCode(callSuper = true)
public class Acceptance extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 验收ID
     */
    private Long id;

    /**
     * 验收编码
     */
    private String acceptanceCode;

    /**
     * 申购ID
     */
    private Long purchaseId;

    /**
     * 供应商ID
     */
    private Long supplierId;

    /**
     * 验收人ID
     */
    private Long acceptancePersonId;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 验收结果
     */
    private String acceptanceResult;

    /**
     * 状态
     */
    private String status;

    /**
     * 验收日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date acceptanceDate;

    /**
     * 合格数量
     */
    private Long qualifiedQuantity;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;
}
