package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Depreciation;
import com.ruoyi.asset.service.IDepreciationService;
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
 * 资产折旧记录管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/depreciation")
public class DepreciationController extends BaseController {

    @Autowired
    private IDepreciationService depreciationService;

    /**
     * 查询资产折旧记录列表（分页）
     */
    @RequiresPermissions("asset:depreciation:list")
    @GetMapping("/list")
    public TableDataInfo list(Depreciation depreciation) {
        startPage();
        List<Depreciation> list = depreciationService.selectDepreciationList(depreciation);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单条折旧记录
     */
    @RequiresPermissions("asset:depreciation:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(depreciationService.getById(id));
    }

    /**
     * 导出资产折旧记录到Excel
     */
    @RequiresPermissions("asset:depreciation:export")
    @Log(title = "资产折旧记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Depreciation depreciation) {
        List<Depreciation> list = depreciationService.selectDepreciationList(depreciation);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setBusinessStatus(DictLabelUtils.getDictLabel("depreciation_status", item.getBusinessStatus()));
        });

        ExcelUtil<Depreciation> util = new ExcelUtil<>(Depreciation.class);
        util.exportExcel(response, list, "资产折旧记录数据");
    }

    /**
     * 新增折旧记录
     */
    @RequiresPermissions("asset:depreciation:add")
    @Log(title = "资产折旧记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Depreciation depreciation) {
        return toAjax(depreciationService.save(depreciation));
    }

    /**
     * 修改折旧记录
     */
    @RequiresPermissions("asset:depreciation:edit")
    @Log(title = "资产折旧记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Depreciation depreciation) {
        return toAjax(depreciationService.updateById(depreciation));
    }

    /**
     * 删除折旧记录
     */
    @RequiresPermissions("asset:depreciation:remove")
    @Log(title = "资产折旧记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(depreciationService.removeByIds(java.util.Arrays.asList(ids)));
    }
}