package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Inventory;
import com.ruoyi.asset.service.IInventoryService;
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
 * 资产盘点管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController extends BaseController {

    @Autowired
    private IInventoryService inventoryService;

    /**
     * 查询盘点任务列表（分页）
     */
    @RequiresPermissions("asset:inventory:list")
    @GetMapping("/list")
    public TableDataInfo list(Inventory inventory) {
        startPage();
        List<Inventory> list = inventoryService.selectInventoryList(inventory);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单个盘点任务
     */
    @RequiresPermissions("asset:inventory:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(inventoryService.getById(id));
    }

    /**
     * 导出盘点任务到Excel
     */
    @RequiresPermissions("asset:inventory:export")
    @Log(title = "盘点管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Inventory inventory) {
        List<Inventory> list = inventoryService.selectInventoryList(inventory);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setInventoryType(DictLabelUtils.getDictLabel("inventory_type", item.getInventoryType()));
            item.setBusinessStatus(DictLabelUtils.getDictLabel("business_status", item.getBusinessStatus()));
        });

        ExcelUtil<Inventory> util = new ExcelUtil<>(Inventory.class);
        util.exportExcel(response, list, "盘点管理数据");
    }

    /**
     * 新增盘点任务
     */
    @RequiresPermissions("asset:inventory:add")
    @Log(title = "盘点管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Inventory inventory) {
        return toAjax(inventoryService.save(inventory));
    }

    /**
     * 修改盘点任务
     */
    @RequiresPermissions("asset:inventory:edit")
    @Log(title = "盘点管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Inventory inventory) {
        return toAjax(inventoryService.updateById(inventory));
    }

    /**
     * 删除盘点任务（同时删除关联的明细）
     */
    @RequiresPermissions("asset:inventory:remove")
    @Log(title = "盘点管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        inventoryService.deleteInventoryDetailByMasterIds(ids);
        return toAjax(inventoryService.removeByIds(java.util.Arrays.asList(ids)));
    }
}