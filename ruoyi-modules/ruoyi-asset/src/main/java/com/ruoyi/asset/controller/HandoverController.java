package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Handover;
import com.ruoyi.asset.service.IHandoverService;
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
 * 资产交接管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/handover")
public class HandoverController extends BaseController {
    @Autowired
    private IHandoverService handoverService;

    /**
     * 查询资产交接列表
     */
    @RequiresPermissions("asset:handover:list")
    @GetMapping("/list")
    public TableDataInfo list(Handover handover) {
        startPage();
        List<Handover> list = handoverService.selectHandoverList(handover);
        return getDataTable(list);
    }

    /**
     * 导出资产交接列表
     */
    @RequiresPermissions("asset:handover:export")
    @Log(title = "资产交接", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Handover handover) {
        List<Handover> list = handoverService.selectHandoverList(handover);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setBusinessStatus(DictLabelUtils.getDictLabel("bill_status", item.getBusinessStatus()));
        });

        ExcelUtil<Handover> util = new ExcelUtil<Handover>(Handover.class);
        util.exportExcel(response, list, "资产交接数据");
    }

    /**
     * 获取资产交接详细信息
     */
    @RequiresPermissions("asset:handover:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(handoverService.getById(id));
    }

    /**
     * 新增资产交接
     */
    @RequiresPermissions("asset:handover:add")
    @Log(title = "资产交接", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Handover handover) {
        return toAjax(handoverService.save(handover));
    }

    /**
     * 修改资产交接
     */
    @RequiresPermissions("asset:handover:edit")
    @Log(title = "资产交接", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Handover handover) {
        return toAjax(handoverService.updateById(handover));
    }

    /**
     * 删除资产交接
     */
    @RequiresPermissions("asset:handover:remove")
    @Log(title = "资产交接", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(handoverService.removeBatchByIds(Arrays.asList(ids)));
    }

    /**
     * 批量审批
     */
    @RequiresPermissions("asset:handover:approve")
    @Log(title = "资产交接审批", businessType = BusinessType.UPDATE)
    @PostMapping("/batchApprove")
    public AjaxResult batchApprove(@RequestBody Map<String, Object> params) {
        return toAjax(handoverService.batchApprove(params));
    }

    /**
     * 统计各状态数量
     */
    @GetMapping("/countByStatus")
    public AjaxResult countByStatus() {
        return success(handoverService.countByStatus());
    }
}
