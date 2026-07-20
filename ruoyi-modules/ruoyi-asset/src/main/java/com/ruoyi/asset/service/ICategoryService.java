package com.ruoyi.asset.service;

import com.ruoyi.asset.domain.Category;

import java.util.List;

/**
 * 资产分类Service接口
 *
 * @author xiaowang
 * @date 2026-03-31
 */
public interface ICategoryService {
    /**
     * 查询资产分类
     *
     * @param id 资产分类主键
     * @return 资产分类
     */
    public Category selectCategoryById(Long id);

    /**
     * 查询资产分类列表
     *
     * @param category 资产分类
     * @return 资产分类集合
     */
    public List<Category> selectCategoryList(Category category);

    /**
     * 新增资产分类
     *
     * @param category 资产分类
     * @return 结果
     */
    public int insertCategory(Category category);

    /**
     * 修改资产分类
     *
     * @param category 资产分类
     * @return 结果
     */
    public int updateCategory(Category category);

    /**
     * 批量删除资产分类
     *
     * @param ids 需要删除的资产分类主键集合
     * @return 结果
     */
    public int deleteCategoryByIds(Long[] ids);

    /**
     * 删除资产分类信息
     *
     * @param id 资产分类主键
     * @return 结果
     */
    public int deleteCategoryById(Long id);
}
