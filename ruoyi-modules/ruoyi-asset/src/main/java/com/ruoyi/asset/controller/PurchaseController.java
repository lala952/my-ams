package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Purchase;
import com.ruoyi.asset.service.IPurchaseService;
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
 * 资产申购管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/purchase")
public class PurchaseController extends BaseController {

    @Autowired
    private IPurchaseService purchaseService;

    /**
     * 查询申购单列表（分页）
     */
    @RequiresPermissions("asset:purchase:list")
    @GetMapping("/list")
    public TableDataInfo list(Purchase purchase) {
        startPage();
        List<Purchase> list = purchaseService.selectPurchaseList(purchase);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单个申购单
     */
    @RequiresPermissions("asset:purchase:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(purchaseService.getById(id));
    }

    /**
     * 导出申购单到Excel
     */
    @RequiresPermissions("asset:purchase:export")
    @Log(title = "资产申购", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Purchase purchase) {
        List<Purchase> list = purchaseService.selectPurchaseList(purchase);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setBusinessStatus(DictLabelUtils.getDictLabel("bill_status", item.getBusinessStatus()));
        });

        ExcelUtil<Purchase> util = new ExcelUtil<>(Purchase.class);
        util.exportExcel(response, list, "资产申购数据");
    }

    /**
     * 新增申购单
     */
    @RequiresPermissions("asset:purchase:add")
    @Log(title = "资产申购", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Purchase purchase) {
        return toAjax(purchaseService.save(purchase));
    }

    /**
     * 修改申购单
     */
    @RequiresPermissions("asset:purchase:edit")
    @Log(title = "资产申购", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Purchase purchase) {
        return toAjax(purchaseService.updateById(purchase));
    }

    /**
     * 删除申购单
     */
    @RequiresPermissions("asset:purchase:remove")
    @Log(title = "资产申购", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(purchaseService.removeByIds(java.util.Arrays.asList(ids)));
    }
}