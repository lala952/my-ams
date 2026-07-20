package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Scrap;
import com.ruoyi.asset.service.IScrapService;
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
 * 资产报废申请管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/scrap")
public class ScrapController extends BaseController {

    @Autowired
    private IScrapService scrapService;

    /**
     * 查询报废申请列表（分页）
     */
    @RequiresPermissions("asset:scrap:list")
    @GetMapping("/list")
    public TableDataInfo list(Scrap scrap) {
        startPage();
        List<Scrap> list = scrapService.selectScrapList(scrap);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单个报废申请
     */
    @RequiresPermissions("asset:scrap:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(scrapService.getById(id));
    }

    /**
     * 导出报废申请到Excel
     */
    @RequiresPermissions("asset:scrap:export")
    @Log(title = "资产报废申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Scrap scrap) {
        List<Scrap> list = scrapService.selectScrapList(scrap);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setBusinessStatus(DictLabelUtils.getDictLabel("bill_status", item.getBusinessStatus()));
        });

        ExcelUtil<Scrap> util = new ExcelUtil<>(Scrap.class);
        util.exportExcel(response, list, "资产报废申请数据");
    }

    /**
     * 新增报废申请
     */
    @RequiresPermissions("asset:scrap:add")
    @Log(title = "资产报废申请", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Scrap scrap) {
        return toAjax(scrapService.save(scrap));
    }

    /**
     * 修改报废申请
     */
    @RequiresPermissions("asset:scrap:edit")
    @Log(title = "资产报废申请", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Scrap scrap) {
        return toAjax(scrapService.updateById(scrap));
    }

    /**
     * 删除报废申请
     */
    @RequiresPermissions("asset:scrap:remove")
    @Log(title = "资产报废申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(scrapService.removeByIds(java.util.Arrays.asList(ids)));
    }
}