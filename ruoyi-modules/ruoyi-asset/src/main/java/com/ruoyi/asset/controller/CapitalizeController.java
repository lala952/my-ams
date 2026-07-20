package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Capitalize;
import com.ruoyi.asset.service.ICapitalizeService;
import com.ruoyi.asset.utils.DictLabelUtils;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 资产转固管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/capitalize")
public class CapitalizeController extends BaseController {

    @Autowired
    private ICapitalizeService capitalizeService;

    /**
     * 查询资产转固列表（分页）
     */
    @RequiresPermissions("asset:capitalize:list")
    @GetMapping("/list")
    public TableDataInfo list(Capitalize capitalize) {
        startPage();
        List<Capitalize> list = capitalizeService.selectCapitalizeList(capitalize);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单个资产转固记录
     */
    @RequiresPermissions("asset:capitalize:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(capitalizeService.getById(id));
    }

    /**
     * 导出资产转固数据到Excel
     */
    @RequiresPermissions("asset:capitalize:export")
    @Log(title = "资产转固", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Capitalize capitalize) {
        List<Capitalize> list = capitalizeService.selectCapitalizeList(capitalize);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setBusinessStatus(DictLabelUtils.getDictLabel("bill_status", item.getBusinessStatus()));
        });

        ExcelUtil<Capitalize> util = new ExcelUtil<>(Capitalize.class);
        util.exportExcel(response, list, "资产转固数据");
    }

    /**
     * 新增资产转固记录
     */
    @RequiresPermissions("asset:capitalize:add")
    @Log(title = "资产转固", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Capitalize capitalize) {
        return toAjax(capitalizeService.save(capitalize));
    }

    /**
     * 修改资产转固记录
     */
    @RequiresPermissions("asset:capitalize:edit")
    @Log(title = "资产转固", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Capitalize capitalize) {
        return toAjax(capitalizeService.updateById(capitalize));
    }

    /**
     * 删除资产转固记录
     */
    @RequiresPermissions("asset:capitalize:remove")
    @Log(title = "资产转固", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(capitalizeService.removeByIds(java.util.Arrays.asList(ids)));
    }
}