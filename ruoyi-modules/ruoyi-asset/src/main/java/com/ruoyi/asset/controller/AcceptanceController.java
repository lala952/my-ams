package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Acceptance;
import com.ruoyi.asset.service.IAcceptanceService;
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
 * 资产验收管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/acceptance")
public class AcceptanceController extends BaseController {

    @Autowired
    private IAcceptanceService acceptanceService;

    /**
     * 查询资产验收列表（分页）
     */
    @RequiresPermissions("asset:acceptance:list")
    @GetMapping("/list")
    public TableDataInfo list(Acceptance acceptance) {
        startPage();
        List<Acceptance> list = acceptanceService.selectAcceptanceList(acceptance);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单个资产验收记录
     */
    @RequiresPermissions("asset:acceptance:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(acceptanceService.getById(id));
    }

    /**
     * 导出资产验收数据到Excel
     */
    @RequiresPermissions("asset:acceptance:export")
    @Log(title = "资产验收", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Acceptance acceptance) {
        List<Acceptance> list = acceptanceService.selectAcceptanceList(acceptance);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setAcceptanceResult(DictLabelUtils.getDictLabel("acceptance_result", item.getAcceptanceResult()));
        });

        ExcelUtil<Acceptance> util = new ExcelUtil<>(Acceptance.class);
        util.exportExcel(response, list, "资产验收数据");
    }

    /**
     * 新增资产验收记录
     */
    @RequiresPermissions("asset:acceptance:add")
    @Log(title = "资产验收", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Acceptance acceptance) {
        return toAjax(acceptanceService.save(acceptance));
    }

    /**
     * 修改资产验收记录
     */
    @RequiresPermissions("asset:acceptance:edit")
    @Log(title = "资产验收", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Acceptance acceptance) {
        return toAjax(acceptanceService.updateById(acceptance));
    }

    /**
     * 删除资产验收记录
     */
    @RequiresPermissions("asset:acceptance:remove")
    @Log(title = "资产验收", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(acceptanceService.removeByIds(java.util.Arrays.asList(ids)));
    }
}