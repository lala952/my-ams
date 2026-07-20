package com.ruoyi.asset.utils;

import com.ruoyi.asset.domain.Category;
import com.ruoyi.asset.service.ICategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class CommonUtils {

    private static final Logger log = LoggerFactory.getLogger(CommonUtils.class);

    private static ICategoryService categoryService;

    @Autowired
    public void setCategoryService(ICategoryService service) {
        categoryService = service;
    }

    /**
     * 格式化金额（千分位分隔，保留两位小数）
     *
     * @param value 金额值
     * @return 格式化后的金额字符串
     */
    public static String formatMoney(BigDecimal value) {
        if (value == null) return "0.00";
        return String.format("%,.2f", value) + " " + "元";
    }

    /**
     * 获取资产分类名称
     *
     * @param categoryId 分类ID
     * @return 分类名称，获取失败返回"-"
     */
    public static String getCategoryName(Long categoryId) {
        log.debug("开始根据categoryId获取分类名称：categoryId为：{}", categoryId);
        if (categoryId == null) {
            return "-";
        }
        try {
            Category category = categoryService.selectCategoryById(categoryId);
            log.debug("根据categoryId获取分类名称成功：categoryId为：{},category为：{}", categoryId, category);
            return category != null ? category.getCategoryName() : "资产分类" + categoryId;
        } catch (Exception e) {
            log.error("根据categoryId获取分类名称失败：categoryId为：{},异常信息为：{}", categoryId, e.getMessage());
            return "-";
        }
    }

    /**
     * 获取部门名称（简化版本，仅用于展示）
     *
     * @param deptId 部门ID
     * @return 部门名称字符串
     */
    public static String getDeptName(Long deptId) {
        return deptId != null ? "部门" + deptId : "-";
    }


    /**
     * 空字符串转"-"
     *
     * @param str 输入字符串
     * @return 非空字符串原值，null或空字符串返回"-"
     */
    public static String nullToEmpty(String str) {
        return str == null || str.isEmpty() ? "-" : str;
    }
}
