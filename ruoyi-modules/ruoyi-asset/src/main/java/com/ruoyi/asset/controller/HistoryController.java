package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.History;
import com.ruoyi.asset.service.IHistoryService;
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
 * 资产履历管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/history")
public class HistoryController extends BaseController {

    @Autowired
    private IHistoryService historyService;

    /**
     * 查询资产履历列表（分页）
     */
    @RequiresPermissions("asset:history:list")
    @GetMapping("/list")
    public TableDataInfo list(History history) {
        startPage();
        List<History> list = historyService.selectHistoryList(history);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单条履历
     */
    @RequiresPermissions("asset:history:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(historyService.getById(id));
    }

    /**
     * 导出资产履历到Excel
     */
    @RequiresPermissions("asset:history:export")
    @Log(title = "资产履历", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, History history) {
        List<History> list = historyService.selectHistoryList(history);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setEventType(DictLabelUtils.getDictLabel("history_event_type", item.getEventType()));
        });

        ExcelUtil<History> util = new ExcelUtil<>(History.class);
        util.exportExcel(response, list, "资产履历数据");
    }

    /**
     * 新增履历记录
     */
    @RequiresPermissions("asset:history:add")
    @Log(title = "资产履历", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody History history) {
        return toAjax(historyService.save(history));
    }

    /**
     * 修改履历记录
     */
    @RequiresPermissions("asset:history:edit")
    @Log(title = "资产履历", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody History history) {
        return toAjax(historyService.updateById(history));
    }

    /**
     * 删除履历记录
     */
    @RequiresPermissions("asset:history:remove")
    @Log(title = "资产履历", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(historyService.removeByIds(java.util.Arrays.asList(ids)));
    }
}