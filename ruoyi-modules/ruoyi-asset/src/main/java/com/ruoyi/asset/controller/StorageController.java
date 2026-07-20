package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Storage;
import com.ruoyi.asset.service.IStorageService;
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
import java.util.Map;

/**
 * 资产入库管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/storage")
public class StorageController extends BaseController {
    @Autowired
    private IStorageService storageService;

    /**
     * 查询资产入库列表（分页）
     */
    @RequiresPermissions("asset:storage:list")
    @GetMapping("/list")
    public TableDataInfo list(Storage storage) {
        startPage();
        List<Storage> list = storageService.selectStorageList(storage);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单个资产入库记录
     */
    @RequiresPermissions("asset:storage:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(storageService.getById(id));
    }

    /**
     * 导出资产入库数据到Excel
     */
    @RequiresPermissions("asset:storage:export")
    @Log(title = "资产入库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Storage storage) {
        List<Storage> list = storageService.selectStorageList(storage);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setBusinessStatus(DictLabelUtils.getDictLabel("bill_status", item.getBusinessStatus()));
        });

        ExcelUtil<Storage> util = new ExcelUtil<>(Storage.class);
        util.exportExcel(response, list, "资产入库数据");
    }

    /**
     * 新增资产入库记录
     */
    @RequiresPermissions("asset:storage:add")
    @Log(title = "资产入库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Storage storage) {
        return toAjax(storageService.save(storage));
    }

    /**
     * 修改资产入库记录
     */
    @RequiresPermissions("asset:storage:edit")
    @Log(title = "资产入库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Storage storage) {
        return toAjax(storageService.updateById(storage));
    }

    /**
     * 删除资产入库记录
     */
    @RequiresPermissions("asset:storage:remove")
    @Log(title = "资产入库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(storageService.removeByIds(java.util.Arrays.asList(ids)));
    }

    /**
     * 批量审批
     */
    @RequiresPermissions("asset:storage:approve")
    @Log(title = "批量审批", businessType = BusinessType.UPDATE)
    @PostMapping("/batchApprove")
    public AjaxResult batchApprove(@RequestBody Map<String, Object> params) {
        return toAjax(storageService.batchApprove(params));
    }

    /**
     * 统计各状态数量
     */
    @GetMapping("/countByStatus")
    public AjaxResult countByStatus() {
        return success(storageService.countByStatus());
    }
}
