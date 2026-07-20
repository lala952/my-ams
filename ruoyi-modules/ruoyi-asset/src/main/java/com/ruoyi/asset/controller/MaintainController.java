package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Maintain;
import com.ruoyi.asset.service.IMaintainService;
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
 * 资产维修管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/maintain")
public class MaintainController extends BaseController {
    @Autowired
    private IMaintainService maintainService;

    /**
     * 查询资产维修列表
     */
    @RequiresPermissions("asset:maintain:list")
    @GetMapping("/list")
    public TableDataInfo list(Maintain maintain) {
        startPage();
        List<Maintain> list = maintainService.selectMaintainList(maintain);
        return getDataTable(list);
    }

    /**
     * 导出资产维修列表
     */
    @RequiresPermissions("asset:maintain:export")
    @Log(title = "资产维修", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Maintain maintain) {
        List<Maintain> list = maintainService.selectMaintainList(maintain);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setMaintenanceType(DictLabelUtils.getDictLabel("maintenance_type", item.getMaintenanceType()));
            item.setBusinessStatus(DictLabelUtils.getDictLabel("bill_status", item.getBusinessStatus()));
        });

        ExcelUtil<Maintain> util = new ExcelUtil<Maintain>(Maintain.class);
        util.exportExcel(response, list, "资产维修数据");
    }

    /**
     * 获取资产维修详细信息
     */
    @RequiresPermissions("asset:maintain:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(maintainService.getById(id));
    }

    /**
     * 新增资产维修
     */
    @RequiresPermissions("asset:maintain:add")
    @Log(title = "资产维修", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Maintain maintain) {
        return toAjax(maintainService.save(maintain));
    }

    /**
     * 修改资产维修
     */
    @RequiresPermissions("asset:maintain:edit")
    @Log(title = "资产维修", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Maintain maintain) {
        return toAjax(maintainService.updateById(maintain));
    }

    /**
     * 删除资产维修
     */
    @RequiresPermissions("asset:maintain:remove")
    @Log(title = "资产维修", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(maintainService.removeBatchByIds(Arrays.asList(ids)));
    }

    /**
     * 批量审批
     */
    @RequiresPermissions("asset:maintain:approve")
    @Log(title = "资产维修审批", businessType = BusinessType.UPDATE)
    @PostMapping("/batchApprove")
    public AjaxResult batchApprove(@RequestBody Map<String, Object> params) {
        return toAjax(maintainService.batchApprove(params));
    }

    /**
     * 统计各状态数量
     */
    @GetMapping("/countByStatus")
    public AjaxResult countByStatus() {
        return success(maintainService.countByStatus());
    }
}
