package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Attachment;
import com.ruoyi.asset.service.IAttachmentService;
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

/**
 * 资产附件管理Controller
 *
 * @author xiaowang
 * @date 2026-03-31
 */
@RestController
@RequestMapping("/attachment")
public class AttachmentController extends BaseController {

    @Autowired
    private IAttachmentService attachmentService;

    /**
     * 查询资产附件列表（分页）
     */
    @RequiresPermissions("asset:attachment:list")
    @GetMapping("/list")
    public TableDataInfo list(Attachment attachment) {
        startPage();
        List<Attachment> list = attachmentService.selectAttachmentList(attachment);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单个附件
     */
    @RequiresPermissions("asset:attachment:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(attachmentService.getById(id));
    }

    /**
     * 导出资产附件数据到Excel
     */
    @RequiresPermissions("asset:attachment:export")
    @Log(title = "资产附件", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Attachment attachment) {
        List<Attachment> list = attachmentService.selectAttachmentList(attachment);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setAttachmentType(DictLabelUtils.getDictLabel("attachment_type", item.getAttachmentType()));
        });

        ExcelUtil<Attachment> util = new ExcelUtil<>(Attachment.class);
        util.exportExcel(response, list, "资产附件数据");
    }

    /**
     * 新增资产附件
     */
    @RequiresPermissions("asset:attachment:add")
    @Log(title = "资产附件", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Attachment attachment) {
        return toAjax(attachmentService.save(attachment));
    }

    /**
     * 修改资产附件
     */
    @RequiresPermissions("asset:attachment:edit")
    @Log(title = "资产附件", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Attachment attachment) {
        return toAjax(attachmentService.updateById(attachment));
    }

    /**
     * 删除资产附件
     */
    @RequiresPermissions("asset:attachment:remove")
    @Log(title = "资产附件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(attachmentService.removeByIds(Arrays.asList(ids)));
    }
}