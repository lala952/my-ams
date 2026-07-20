package com.ruoyi.asset.controller;

import java.util.List;
import java.util.Arrays;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.asset.domain.SplitResult;
import com.ruoyi.asset.service.ISplitResultService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.page.TableDataInfo;

/**
 * 资产拆分结果Controller(MybatisPlus)
 *
 * @author xiaowang
 * @date 2026-06-16
 */
@RestController
@RequestMapping("/splitResult" )
public class SplitResultController extends BaseController {
    @Autowired
    private ISplitResultService splitResultService;

/**
 * 查询资产拆分结果列表
 */
@RequiresPermissions("asset:splitResult:list" )
@GetMapping("/list" )
    public TableDataInfo list(SplitResult splitResult) {
        startPage();
        List<SplitResult> list = splitResultService.selectSplitResultList(splitResult);
        return getDataTable(list);
    }

    /**
     * 导出资产拆分结果列表
     */
    @RequiresPermissions("asset:splitResult:export" )
    @Log(title = "资产拆分结果" , businessType = BusinessType.EXPORT)
    @PostMapping("/export" )
    public void export(HttpServletResponse response, SplitResult splitResult) {
        List<SplitResult> list = splitResultService.selectSplitResultList(splitResult);
        ExcelUtil<SplitResult> util = new ExcelUtil<SplitResult>(SplitResult. class);
        util.exportExcel(response, list, "资产拆分结果数据" );
    }

    /**
     * 获取资产拆分结果详细信息
     */
    @RequiresPermissions("asset:splitResult:query" )
    @GetMapping(value = "/{id}" )
    public AjaxResult getInfo(@PathVariable("id" ) Long id) {
        return success(splitResultService.getById(id));
    }


    /**
     * 新增资产拆分结果
    */
    @RequiresPermissions("asset:splitResult:add" )
    @Log(title = "资产拆分结果" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SplitResult splitResult) {
        return toAjax(splitResultService.save(splitResult));
    }

    /**
     * 修改资产拆分结果
     */

    @RequiresPermissions("asset:splitResult:edit" )
    @Log(title = "资产拆分结果" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SplitResult splitResult) {
        return toAjax(splitResultService.updateById(splitResult));
    }

    /**
     * 删除资产拆分结果
     */
    @RequiresPermissions("asset:splitResult:remove" )
    @Log(title = "资产拆分结果" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}" )
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(splitResultService.removeByIds(Arrays.asList(ids)));
    }
}