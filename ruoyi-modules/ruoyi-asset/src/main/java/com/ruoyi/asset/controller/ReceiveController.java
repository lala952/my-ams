package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Receive;
import com.ruoyi.asset.service.IReceiveService;
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
 * 资产领用管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/receive")
public class ReceiveController extends BaseController {

    @Autowired
    private IReceiveService receiveService;

    /**
     * 查询资产领用列表
     */
    @RequiresPermissions("asset:receive:list")
    @GetMapping("/list")
    public TableDataInfo list(Receive receive) {
        startPage();
        List<Receive> list = receiveService.selectReceiveList(receive);
        return getDataTable(list);
    }

    /**
     * 导出资产领用列表
     */
    @RequiresPermissions("asset:receive:export")
    @Log(title = "资产领用", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Receive receive) {
        List<Receive> list = receiveService.selectReceiveList(receive);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setBusinessStatus(DictLabelUtils.getDictLabel("bill_status", item.getBusinessStatus()));
        });

        ExcelUtil<Receive> util = new ExcelUtil<Receive>(Receive.class);
        util.exportExcel(response, list, "资产领用数据");
    }

    /**
     * 获取资产领用详细信息
     */
    @RequiresPermissions("asset:receive:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(receiveService.getById(id));
    }

    /**
     * 新增资产领用
     */
    @RequiresPermissions("asset:receive:add")
    @Log(title = "资产领用", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Receive receive) {
        return toAjax(receiveService.save(receive));
    }

    /**
     * 修改资产领用
     */
    @RequiresPermissions("asset:receive:edit")
    @Log(title = "资产领用", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Receive receive) {
        return toAjax(receiveService.updateById(receive));
    }

    /**
     * 删除资产领用
     */
    @RequiresPermissions("asset:receive:remove")
    @Log(title = "资产领用", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(receiveService.removeBatchByIds(Arrays.asList(ids)));
    }

    /**
     * 批量审批
     */
    @RequiresPermissions("asset:receive:approve")
    @Log(title = "资产领用审批", businessType = BusinessType.UPDATE)
    @PostMapping("/batchApprove")
    public AjaxResult batchApprove(@RequestBody Map<String, Object> params) {
        return toAjax(receiveService.batchApprove(params));
    }

    /**
     * 统计各状态数量
     */
    @GetMapping("/countByStatus")
    public AjaxResult countByStatus() {
        return success(receiveService.countByStatus());
    }
}
