package com.ruoyi.asset.domain.vo;

import com.ruoyi.asset.domain.Assets;
import com.ruoyi.asset.domain.Change;
import com.ruoyi.asset.domain.ChangeAttachment;
import com.ruoyi.common.core.annotation.Excel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 资产变动VO
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChangeVO extends Change {

    private static final long serialVersionUID = 1L;

    /**
     * 申请人姓名
     */
    @Excel(name = "申请人", defaultValue = "无")
    private String applicantName;

    /**
     * 申请部门名称
     */
    @Excel(name = "申请部门", defaultValue = "无")
    private String applicantDeptName;

    /**
     * 资产清单
     */
    private List<Assets> assets;

    /**
     * 附件清单
     */
    private List<ChangeAttachment> attachments;

}