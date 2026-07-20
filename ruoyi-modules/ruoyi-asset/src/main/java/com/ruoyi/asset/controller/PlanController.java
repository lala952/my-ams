package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Plan;
import com.ruoyi.asset.service.IPlanService;
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
 * 资产配置计划管理Controller
 *
 * @author xiaowang
 * @date 2026-05-07
 */
@RestController
@RequestMapping("/plan")
public class PlanController extends BaseController {
    @Autowired
    private IPlanService planService;

    /**
     * 查询配置计划列表（分页）
     */
    @RequiresPermissions("asset:plan:list")
    @GetMapping("/list")
    public TableDataInfo list(Plan plan) {
        startPage();
        List<Plan> list = planService.selectPlanList(plan);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单个配置计划
     */
    @RequiresPermissions("asset:plan:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(planService.getById(id));
    }

    /**
     * 根据多个ID批量查询配置计划
     */
    @RequiresPermissions("asset:plan:query")
    @GetMapping(value = "/ids")
    public AjaxResult getInfoByIds(@RequestParam("ids") Long[] ids) {
        if (ids == null || ids.length == 0) {
            return success(new java.util.ArrayList<>());
        }
        return success(planService.selectPlanByIds(ids));
    }

    /**
     * 新增配置计划
     */
    @RequiresPermissions("asset:plan:add")
    @Log(title = "资产配置计划", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Plan plan) {
        return toAjax(planService.save(plan));
    }

    /**
     * 修改配置计划
     */
    @RequiresPermissions("asset:plan:edit")
    @Log(title = "资产配置计划", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Plan plan) {
        return toAjax(planService.updateById(plan));
    }

    /**
     * 删除配置计划
     */
    @RequiresPermissions("asset:plan:remove")
    @Log(title = "资产配置计划", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(planService.removeByIds(java.util.Arrays.asList(ids)));
    }

    /**
     * 导出配置计划数据到Excel
     */
    @RequiresPermissions("asset:plan:export")
    @Log(title = "资产配置计划", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Plan plan) {
        List<Plan> list = planService.selectPlanList(plan);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setBusinessStatus(DictLabelUtils.getDictLabel("bill_status", item.getBusinessStatus()));
        });

        ExcelUtil<Plan> util = new ExcelUtil<>(Plan.class);
        util.exportExcel(response, list, "资产配置计划数据");
    }

    /**
     * 提交审批
     */
    @RequiresPermissions("asset:plan:submit")
    @Log(title = "资产配置计划提交审批", businessType = BusinessType.UPDATE)
    @PostMapping("/submit/{id}")
    public AjaxResult submit(@PathVariable Long id) {
        return toAjax(planService.submitPlan(id));
    }

    /**
     * 生成采购单
     */
    @RequiresPermissions("asset:plan:submit")
    @Log(title = "资产配置计划生成采购单", businessType = BusinessType.OTHER)
    @PostMapping("/generatePurchase/{id}")
    public AjaxResult generatePurchase(@PathVariable Long id) {
        return toAjax(planService.generatePurchase(id));
    }
}
