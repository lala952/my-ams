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
import com.ruoyi.asset.domain.Split;
import com.ruoyi.asset.service.ISplitService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.page.TableDataInfo;

/**
 * 资产拆分明细Controller(MybatisPlus)
 *
 * @author xiaowang
 * @date 2026-06-16
 */
@RestController
@RequestMapping("/split" )
public class SplitController extends BaseController {
    @Autowired
    private ISplitService splitService;

/**
 * 查询资产拆分明细列表
 */
@RequiresPermissions("asset:split:list" )
@GetMapping("/list" )
    public TableDataInfo list(Split split) {
        startPage();
        List<Split> list = splitService.selectSplitList(split);
        return getDataTable(list);
    }

    /**
     * 导出资产拆分明细列表
     */
    @RequiresPermissions("asset:split:export" )
    @Log(title = "资产拆分明细" , businessType = BusinessType.EXPORT)
    @PostMapping("/export" )
    public void export(HttpServletResponse response, Split split) {
        List<Split> list = splitService.selectSplitList(split);
        ExcelUtil<Split> util = new ExcelUtil<Split>(Split. class);
        util.exportExcel(response, list, "资产拆分明细数据" );
    }

    /**
     * 获取资产拆分明细详细信息
     */
    @RequiresPermissions("asset:split:query" )
    @GetMapping(value = "/{id}" )
    public AjaxResult getInfo(@PathVariable("id" ) Long id) {
        return success(splitService.getById(id));
    }


    /**
     * 新增资产拆分明细
    */
    @RequiresPermissions("asset:split:add" )
    @Log(title = "资产拆分明细" , businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Split split) {
        return toAjax(splitService.save(split));
    }

    /**
     * 修改资产拆分明细
     */

    @RequiresPermissions("asset:split:edit" )
    @Log(title = "资产拆分明细" , businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Split split) {
        return toAjax(splitService.updateById(split));
    }

    /**
     * 删除资产拆分明细
     */
    @RequiresPermissions("asset:split:remove" )
    @Log(title = "资产拆分明细" , businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}" )
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(splitService.removeByIds(Arrays.asList(ids)));
    }
}