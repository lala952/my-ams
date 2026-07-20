package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.InventoryDetail;
import com.ruoyi.asset.service.IInventoryDetailService;
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
 * 资产盘点明细管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/inventoryDetail")
public class InventoryDetailController extends BaseController {
    @Autowired
    private IInventoryDetailService inventoryDetailService;

    /**
     * 查询盘点明细列表（分页）
     */
    @RequiresPermissions("asset:inventoryDetail:list")
    @GetMapping("/list")
    public TableDataInfo list(InventoryDetail inventoryDetail) {
        startPage();
        List<InventoryDetail> list = inventoryDetailService.selectInventoryDetailList(inventoryDetail);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单条盘点明细
     */
    @RequiresPermissions("asset:inventoryDetail:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(inventoryDetailService.getById(id));
    }

    /**
     * 导出盘点明细到Excel
     */
    @RequiresPermissions("asset:inventoryDetail:export")
    @Log(title = "资产盘点明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, InventoryDetail inventoryDetail) {
        List<InventoryDetail> list = inventoryDetailService.selectInventoryDetailList(inventoryDetail);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setInventoryResult(DictLabelUtils.getDictLabel("inventory_result", item.getInventoryResult()));
        });

        ExcelUtil<InventoryDetail> util = new ExcelUtil<>(InventoryDetail.class);
        util.exportExcel(response, list, "资产盘点明细数据");
    }

    /**
     * 新增盘点明细
     */
    @RequiresPermissions("asset:inventoryDetail:add")
    @Log(title = "资产盘点明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody InventoryDetail inventoryDetail) {
        return toAjax(inventoryDetailService.save(inventoryDetail));
    }

    /**
     * 修改盘点明细
     */
    @RequiresPermissions("asset:inventoryDetail:edit")
    @Log(title = "资产盘点明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody InventoryDetail inventoryDetail) {
        return toAjax(inventoryDetailService.updateById(inventoryDetail));
    }

    /**
     * 删除盘点明细
     */
    @RequiresPermissions("asset:inventoryDetail:remove")
    @Log(title = "资产盘点明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(inventoryDetailService.removeByIds(java.util.Arrays.asList(ids)));
    }
}