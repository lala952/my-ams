package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Transfer;
import com.ruoyi.asset.service.ITransferService;
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
 * 资产调拨管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/transfer")
public class TransferController extends BaseController {
    @Autowired
    private ITransferService transferService;

    /**
     * 查询资产调拨列表
     */
    @RequiresPermissions("asset:transfer:list")
    @GetMapping("/list")
    public TableDataInfo list(Transfer transfer) {
        startPage();
        List<Transfer> list = transferService.selectTransferList(transfer);
        return getDataTable(list);
    }

    /**
     * 导出资产调拨列表
     */
    @RequiresPermissions("asset:transfer:export")
    @Log(title = "资产调拨", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Transfer transfer) {
        List<Transfer> list = transferService.selectTransferList(transfer);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setBusinessStatus(DictLabelUtils.getDictLabel("bill_status", item.getBusinessStatus()));
        });

        ExcelUtil<Transfer> util = new ExcelUtil<Transfer>(Transfer.class);
        util.exportExcel(response, list, "资产调拨数据");
    }

    /**
     * 获取资产调拨详细信息
     */
    @RequiresPermissions("asset:transfer:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(transferService.getById(id));
    }

    /**
     * 新增资产调拨
     */
    @RequiresPermissions("asset:transfer:add")
    @Log(title = "资产调拨", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Transfer transfer) {
        return toAjax(transferService.save(transfer));
    }

    /**
     * 修改资产调拨
     */
    @RequiresPermissions("asset:transfer:edit")
    @Log(title = "资产调拨", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Transfer transfer) {
        return toAjax(transferService.updateById(transfer));
    }

    /**
     * 删除资产调拨
     */
    @RequiresPermissions("asset:transfer:remove")
    @Log(title = "资产调拨", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(transferService.removeBatchByIds(Arrays.asList(ids)));
    }

    /**
     * 批量审批
     */
    @RequiresPermissions("asset:transfer:approve")
    @Log(title = "资产调拨审批", businessType = BusinessType.UPDATE)
    @PostMapping("/batchApprove")
    public AjaxResult batchApprove(@RequestBody Map<String, Object> params) {
        return toAjax(transferService.batchApprove(params));
    }

    /**
     * 统计各状态数量
     */
    @GetMapping("/countByStatus")
    public AjaxResult countByStatus() {
        return success(transferService.countByStatus());
    }
}
