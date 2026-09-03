package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Category;
import com.ruoyi.asset.service.ICategoryService;
import com.ruoyi.asset.utils.DictLabelUtils;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 资产分类Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/category")
public class CategoryController extends BaseController {
    @Autowired
    private ICategoryService categoryService;

    /**
     * 查询资产分类列表
     *
     * @param category 资产分类查询条件
     * @return 资产分类分页列表
     */
    @RequiresPermissions("asset:category:list")
    @GetMapping("/list")
    public AjaxResult list(Category category) {
        List<Category> list = categoryService.selectCategoryList(category);
        return success(list);
    }

    /**
     * 导出资产分类列表
     *
     * @param response HTTP响应对象
     * @param category 资产分类查询条件
     */
    @RequiresPermissions("asset:category:export")
    @Log(title = "资产分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Category category) {
        List<Category> list = categoryService.selectCategoryList(category);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setDepreciationMethod(DictLabelUtils.getDictLabel("depreciation_method", item.getDepreciationMethod()));
        });

        ExcelUtil<Category> util = new ExcelUtil<Category>(Category.class);
        util.exportExcel(response, list, "资产分类数据");
    }

    /**
     * 获取资产分类详细信息
     *
     * @param id 资产分类ID
     * @return 资产分类详细信息
     */
    @RequiresPermissions("asset:category:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(categoryService.selectCategoryById(id));
    }

    /**
     * 新增资产分类
     *
     * @param category 资产分类信息
     * @return 操作结果
     */
    @RequiresPermissions("asset:category:add")
    @Log(title = "资产分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Category category) {
        return toAjax(categoryService.insertCategory(category));
    }

    /**
     * 修改资产分类
     *
     * @param category 资产分类信息
     * @return 操作结果
     */
    @RequiresPermissions("asset:category:edit")
    @Log(title = "资产分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Category category) {
        return toAjax(categoryService.updateCategory(category));
    }

    /**
     * 删除资产分类
     *
     * @param ids 需要删除的资产分类ID数组
     * @return 操作结果
     */
    @RequiresPermissions("asset:category:remove")
    @Log(title = "资产分类", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(categoryService.deleteCategoryByIds(ids));
    }
}
