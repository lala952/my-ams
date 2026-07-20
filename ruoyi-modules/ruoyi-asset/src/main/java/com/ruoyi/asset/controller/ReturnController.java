package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Return;
import com.ruoyi.asset.service.IReturnService;
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
 * 资产交回管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/return")
public class ReturnController extends BaseController {
    @Autowired
    private IReturnService returnService;

    /**
     * 查询资产交回列表
     */
    @RequiresPermissions("asset:return:list")
    @GetMapping("/list")
    public TableDataInfo list(Return aReturn) {
        startPage();
        List<Return> list = returnService.selectReturnList(aReturn);
        return getDataTable(list);
    }

    /**
     * 导出资产交回列表
     */
    @RequiresPermissions("asset:return:export")
    @Log(title = "资产交回", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Return aReturn) {
        List<Return> list = returnService.selectReturnList(aReturn);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setBusinessStatus(DictLabelUtils.getDictLabel("bill_status", item.getBusinessStatus()));
        });

        ExcelUtil<Return> util = new ExcelUtil<Return>(Return.class);
        util.exportExcel(response, list, "资产交回数据");
    }

    /**
     * 获取资产交回详细信息
     */
    @RequiresPermissions("asset:return:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(returnService.getById(id));
    }

    /**
     * 新增资产交回
     */
    @RequiresPermissions("asset:return:add")
    @Log(title = "资产交回", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Return aReturn) {
        return toAjax(returnService.save(aReturn));
    }

    /**
     * 修改资产交回
     */
    @RequiresPermissions("asset:return:edit")
    @Log(title = "资产交回", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Return aReturn) {
        return toAjax(returnService.updateById(aReturn));
    }

    /**
     * 删除资产交回
     */
    @RequiresPermissions("asset:return:remove")
    @Log(title = "资产交回", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(returnService.removeBatchByIds(Arrays.asList(ids)));
    }

    /**
     * 批量审批
     */
    @RequiresPermissions("asset:return:approve")
    @Log(title = "资产交回审批", businessType = BusinessType.UPDATE)
    @PostMapping("/batchApprove")
    public AjaxResult batchApprove(@RequestBody Map<String, Object> params) {
        return toAjax(returnService.batchApprove(params));
    }

    /**
     * 统计各状态数量
     */
    @GetMapping("/countByStatus")
    public AjaxResult countByStatus() {
        return success(returnService.countByStatus());
    }
}
