package com.ruoyi.workflow.api.constant;

/**
 * 业务状态常量
 * 用于表示工作流单据的整体业务状态
 *
 * @author ruoyi
 * @date 2026-09-03
 */
public class BusinessStatusConstants {

    /** 待提交 */
    public final static String DRAFT = "draft";

    /** 办理中 */
    public final static String PENDING = "pending";

    /** 已退回 */
    public final static String REJECTED = "rejected";

    /** 已完成 */
    public final static String COMPLETED = "completed";
}
