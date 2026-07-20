package com.ruoyi.asset.controller;

import com.ruoyi.asset.constant.DictConstants;
import com.ruoyi.asset.domain.Change;
import com.ruoyi.asset.domain.dto.ApproveDTO;
import com.ruoyi.asset.domain.dto.BatchApproveDTO;
import com.ruoyi.asset.domain.vo.ChangeVO;
import com.ruoyi.asset.service.IChangeService;
import com.ruoyi.asset.utils.DictLabelUtils;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/change")
public class ChangeController extends BaseController {

    @Autowired
    private IChangeService changeService;

    @RequiresPermissions("asset:change:list")
    @GetMapping("/list")
    public TableDataInfo list(Change change) {
        startPage();
        List<ChangeVO> list = changeService.selectChangeList(change);
        return getDataTable(list);
    }

    @RequiresPermissions("asset:change:list")
    @GetMapping("/countByStatus")
    public AjaxResult countByStatus() {
        return success(changeService.countByStatus());
    }

    @RequiresPermissions("asset:change:query")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        ChangeVO change = changeService.selectChangeById(id);
        return change == null ? error("变动单不存在") : success(change);
    }

    @RequiresPermissions("asset:change:export")
    @Log(title = "资产变动", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ChangeVO change) {
        List<ChangeVO> list = changeService.selectChangeList(change);

        list.forEach(item -> {
            item.setChangeType(DictLabelUtils.getDictLabel(DictConstants.CHANGE_TYPE, item.getChangeType()));
            item.setBusinessStatus(DictLabelUtils.getDictLabel(DictConstants.BUSINESS_STATUS, item.getBusinessStatus()));
        });

        ExcelUtil<ChangeVO> util = new ExcelUtil<>(ChangeVO.class);
        util.exportExcel(response, list, "资产变动数据");
    }

    @RequiresPermissions("asset:change:export")
    @Log(title = "资产变动PDF", businessType = BusinessType.EXPORT)
    @GetMapping("/pdf/{id}")
    public void exportPdf(HttpServletResponse response, @PathVariable Long id) throws Exception {
        changeService.exportPdf(response, id);
    }

    @RequiresPermissions("asset:change:add")
    @Log(title = "资产变动", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody ChangeVO change) {
        changeService.insertChange(change);
        return success(change.getId());
    }

    @RequiresPermissions("asset:change:edit")
    @Log(title = "资产变动", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody ChangeVO change) {
        return toAjax(changeService.updateChange(change));
    }

    @RequiresPermissions("asset:change:remove")
    @Log(title = "资产变动", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(changeService.deleteChangeByIds(ids));
    }

    @PostMapping("/draft/save")
    @RequiresPermissions("asset:change:add")
    public AjaxResult saveDraft(@RequestBody ChangeVO change) {
        return success(changeService.saveDraft(change));
    }

    @Log(title = "资产变动提交", businessType = BusinessType.UPDATE)
    @RequiresPermissions("asset:change:submit")
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody ChangeVO change) {
        return success(changeService.submitChange(change));
    }

    @RequiresPermissions("asset:change:approve")
    @Log(title = "资产变动审批", businessType = BusinessType.UPDATE)
    @PostMapping("/approve")
    public AjaxResult approve(@Validated @RequestBody ApproveDTO dto) {
        return toAjax(changeService.approveChange(dto.getId(), dto.getResult(), dto.getComment(), SecurityUtils.getUserId()));
    }

    @RequiresPermissions("asset:change:approve")
    @Log(title = "资产变动批量审批", businessType = BusinessType.UPDATE)
    @PostMapping("/batchApprove")
    public AjaxResult batchApprove(@Validated @RequestBody BatchApproveDTO dto) {
        Long approverId = SecurityUtils.getUserId();
        int successCount = 0;
        int failCount = 0;
        StringBuilder errorMsg = new StringBuilder();

        for (Long id : dto.getIds()) {
            try {
                changeService.approveChange(id, dto.getResult(), dto.getComment(), approverId);
                successCount++;
            } catch (Exception e) {
                failCount++;
                errorMsg.append("[").append(id).append("]").append(e.getMessage()).append("; ");
            }
        }
        String result = String.format("成功%d条，失败%d条", successCount, failCount);
        return failCount > 0 ? error(result + "。" + errorMsg) : success(result);
    }

    @RequiresPermissions("asset:change:withdraw")
    @Log(title = "资产变动撤回", businessType = BusinessType.UPDATE)
    @PostMapping("/withdraw/{id}")
    public AjaxResult withdraw(@PathVariable Long id) {
        return toAjax(changeService.withdrawChange(id));
    }
}