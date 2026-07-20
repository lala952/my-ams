package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Supplier;
import com.ruoyi.asset.service.ISupplierService;
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
 * 供应商管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/supplier")
public class SupplierController extends BaseController {

    @Autowired
    private ISupplierService supplierService;

    /**
     * 查询供应商列表（分页）
     */
    @GetMapping("/list")
    public TableDataInfo list(Supplier supplier) {
        startPage();
        List<Supplier> list = supplierService.selectSupplierList(supplier);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单个供应商
     */
    @RequiresPermissions("asset:supplier:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(supplierService.getById(id));
    }

    /**
     * 导出供应商数据到Excel
     */
    @RequiresPermissions("asset:supplier:export")
    @Log(title = "供应商", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Supplier supplier) {
        List<Supplier> list = supplierService.selectSupplierList(supplier);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setSupplierType(DictLabelUtils.getDictLabel("supplier_type", item.getSupplierType()));
            item.setCreditRating(DictLabelUtils.getDictLabel("credit_rating", item.getCreditRating()));
            item.setCooperationStatus(DictLabelUtils.getDictLabel("cooperation_status", item.getCooperationStatus()));
        });

        ExcelUtil<Supplier> util = new ExcelUtil<>(Supplier.class);
        util.exportExcel(response, list, "供应商数据");
    }

    /**
     * 新增供应商
     */
    @RequiresPermissions("asset:supplier:add")
    @Log(title = "供应商", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Supplier supplier) {
        return toAjax(supplierService.save(supplier));
    }

    /**
     * 修改供应商
     */
    @RequiresPermissions("asset:supplier:edit")
    @Log(title = "供应商", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Supplier supplier) {
        return toAjax(supplierService.updateById(supplier));
    }

    /**
     * 删除供应商
     */
    @RequiresPermissions("asset:supplier:remove")
    @Log(title = "供应商", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(supplierService.removeByIds(java.util.Arrays.asList(ids)));
    }
}