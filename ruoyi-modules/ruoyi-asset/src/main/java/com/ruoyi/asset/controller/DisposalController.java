package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Disposal;
import com.ruoyi.asset.service.IDisposalService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 资产处置管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/disposal")
public class DisposalController extends BaseController {
    @Autowired
    private IDisposalService disposalService;

    /**
     * 查询资产处置列表
     */
    @RequiresPermissions("asset:disposal:list")
    @GetMapping("/list")
    public TableDataInfo list(Disposal disposal) {
        startPage();
        List<Disposal> list = disposalService.selectDisposalList(disposal);
        return getDataTable(list);
    }

    /**
     * 导出资产处置列表
     */
    @RequiresPermissions("asset:disposal:export")
    @Log(title = "资产处置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Disposal disposal) {
        List<Disposal> list = disposalService.selectDisposalList(disposal);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setBusinessStatus(DictLabelUtils.getDictLabel("bill_status", item.getBusinessStatus()));
        });

        ExcelUtil<Disposal> util = new ExcelUtil<Disposal>(Disposal.class);
        util.exportExcel(response, list, "资产处置数据");
    }

    /**
     * 获取资产处置详细信息
     */
    @RequiresPermissions("asset:disposal:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(disposalService.getById(id));
    }

    /**
     * 新增资产处置
     */
    @RequiresPermissions("asset:disposal:add")
    @Log(title = "资产处置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Disposal disposal) {
        return toAjax(disposalService.save(disposal));
    }

    /**
     * 修改资产处置
     */
    @RequiresPermissions("asset:disposal:edit")
    @Log(title = "资产处置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Disposal disposal) {
        return toAjax(disposalService.updateById(disposal));
    }

    /**
     * 删除资产处置
     */
    @RequiresPermissions("asset:disposal:remove")
    @Log(title = "资产处置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(disposalService.removeBatchByIds(Arrays.asList(ids)));
    }

    /**
     * 批量审批
     */
    @RequiresPermissions("asset:disposal:approve")
    @Log(title = "资产处置审批", businessType = BusinessType.UPDATE)
    @PostMapping("/batchApprove")
    public AjaxResult batchApprove(@RequestBody Map<String, Object> params) {
        return toAjax(disposalService.batchApprove(params));
    }

    /**
     * 统计各状态数量
     */
    @GetMapping("/countByStatus")
    public AjaxResult countByStatus() {
        return success(disposalService.countByStatus());
    }
}
