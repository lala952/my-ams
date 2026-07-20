package com.ruoyi.asset.controller;

import com.ruoyi.asset.domain.Assets;
import com.ruoyi.asset.service.IAssetsService;
import com.ruoyi.asset.utils.AssetsPdf;
import com.ruoyi.asset.utils.DictLabelUtils;
import com.ruoyi.asset.utils.GenerateCode;
import com.ruoyi.asset.utils.ZxingUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资产台账管理Controller
 *
 * @author xiaowang
 * @date 2026-03-30
 */
@RestController
@RequestMapping("/assets")
public class AssetsController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(AssetsController.class);

    @Autowired
    private IAssetsService assetsService;

    @Autowired
    private GenerateCode generateCode;
    @Autowired
    private AssetsPdf assetsPdf;

    /**
     * 查询名下资产列表（分页）- 按当前登录用户过滤
     */
    @RequiresPermissions("asset:my:list")
    @GetMapping("/my/list")
    public TableDataInfo listMyAssets(Assets assets) {
        Long userId = SecurityUtils.getUserId();
        if (userId == null) {
            return getDataTable(new ArrayList<>());
        }
        assets.setUserId(userId);
        startPage();
        List<Assets> list = assetsService.selectAssetsList(assets);
        return getDataTable(list);
    }

    /**
     * 根据ID查询名下资产详情
     */
    @RequiresPermissions("asset:my:query")
    @GetMapping("/my/{id}")
    public AjaxResult getMyAssetInfo(@PathVariable("id") Long id) {
        Long userId = SecurityUtils.getUserId();
        Assets asset = assetsService.queryById(id);

        if (asset == null) {
            return error("当前名下无相关资产");
        }

        if (!userId.equals(asset.getUserId())) {
            return error("无权查看该资产");
        }

        return success(asset);
    }

    /**
     * 查询资产列表（分页）
     */
    @RequiresPermissions("asset:assets:list")
    @GetMapping("/list")
    public TableDataInfo list(Assets assets) {
        startPage();
        List<Assets> list = assetsService.selectAssetsList(assets);
        return getDataTable(list);
    }

    /**
     * 根据ID查询单个资产
     */
    @RequiresPermissions("asset:assets:query")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(assetsService.queryById(id));
    }

    /**
     * 根据多个ID批量查询资产
     */
    @RequiresPermissions("asset:assets:query")
    @GetMapping(value = "/ids")
    public AjaxResult getInfoByIds(@RequestParam("ids") Long[] ids) {
        if (ids == null || ids.length == 0) {
            return success(new ArrayList<>());
        }
        return success(assetsService.selectAssetsByIds(ids));
    }

    /**
     * 新增资产
     */
    @RequiresPermissions("asset:assets:add")
    @Log(title = "资产台账", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Assets assets) {
        log.info("【资产台账-新增】开始，资产名称：{}", assets.getAssetName());
        
        if (StringUtils.isEmpty(assets.getAssetCode())) {
            assets.setAssetCode(generateCode.generateCode("ZCTZ"));
            log.info("【资产台账-新增】自动生成资产编码：{}", assets.getAssetCode());
        }
        
        boolean result = assetsService.save(assets);
        if (result) {
            log.info("【资产台账-新增】成功，资产ID：{}，资产编码：{}", assets.getId(), assets.getAssetCode());
        } else {
            log.error("【资产台账-新增】失败，资产名称：{}", assets.getAssetName());
        }
        return toAjax(result);
    }

    /**
     * 修改资产
     */
    @RequiresPermissions("asset:assets:edit")
    @Log(title = "资产台账", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Assets assets) {
        log.info("【资产台账-修改】开始，资产ID：{}，资产编码：{}", assets.getId(), assets.getAssetCode());
        
        boolean result = assetsService.updateById(assets);
        if (result) {
            log.info("【资产台账-修改】成功，资产ID：{}", assets.getId());
        } else {
            log.error("【资产台账-修改】失败，资产ID：{}", assets.getId());
        }
        return toAjax(result);
    }

    /**
     * 删除资产
     */
    @RequiresPermissions("asset:assets:remove")
    @Log(title = "资产台账", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        log.info("【资产台账-删除】开始，删除数量：{}，IDs：{}", ids.length, Arrays.toString(ids));
        
        boolean result = assetsService.removeByIds(Arrays.asList(ids));
        if (result) {
            log.info("【资产台账-删除】成功，删除数量：{}", ids.length);
        } else {
            log.error("【资产台账-删除】失败，IDs：{}", Arrays.toString(ids));
        }
        return toAjax(result);
    }

    /**
     * 导出资产数据到Excel
     */
    @RequiresPermissions("asset:assets:export")
    @Log(title = "资产台账", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Assets assets) {
        List<Assets> list = assetsService.selectAssetsList(assets);

        // 转换字典值为标签
        list.forEach(item -> {
            item.setAssetType(DictLabelUtils.getDictLabel("asset_type", item.getAssetType()));
            item.setAssetSource(DictLabelUtils.getDictLabel("asset_source", item.getAssetSource()));
            item.setDepreciationMethod(DictLabelUtils.getDictLabel("depreciation_method", item.getDepreciationMethod()));
            item.setUnit(DictLabelUtils.getDictLabel("measure_unit", item.getUnit()));
            item.setAssetStatus(DictLabelUtils.getDictLabel("asset_status", item.getAssetStatus()));
        });

        ExcelUtil<Assets> util = new ExcelUtil<>(Assets.class);
        util.exportExcel(response, list, "资产台账数据");
    }

    /**
     * 导出资产PDF
     */
    @RequiresPermissions("asset:assets:export")
    @Log(title = "资产台账", businessType = BusinessType.EXPORT)
    @GetMapping("/pdf/{id}")
    public void exportPdf(@PathVariable Long id, HttpServletResponse response) throws Exception {
        Assets asset = assetsService.getById(id);
        if (asset == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "资产不存在");
            return;
        }
        assetsPdf.exportPdf(response, asset);
    }

    /** 批量导出资产台账二维码PDF */
    @RequiresPermissions("asset:assets:export")
    @Log(title = "资产台账", businessType = BusinessType.EXPORT)
    @PostMapping("export/barcodes/pdf")
    public void exportBarcodesPdf(HttpServletResponse response,Assets assets) throws IOException {
        List<Assets> list = assetsService.selectAssetsList(assets);
        AssetsPdf util = new AssetsPdf();
        util.exportBarcodePdf(response,list);
    }


    /**
     * 预览二维码（图片直接在浏览器显示）
     */
    @GetMapping("/qrcode/preview/{id}")
    public void previewQrCode(@PathVariable Long id, HttpServletResponse response) throws Exception {
        log.info("【资产台账-二维码预览】开始，资产ID：{}", id);

        Assets asset = assetsService.getById(id);
        if (asset == null) {
            log.error("【资产台账-二维码预览】资产不存在，ID：{}", id);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "资产不存在");
            return;
        }

        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        String baseUrl = "http://localhost:80";
        String qrContent = baseUrl + "/asset/assets/view?id=" + asset.getId();

        BufferedImage image = ZxingUtils.createQRCode(qrContent, 350, 350);
        if (image == null) {
            log.error("【资产台账-二维码预览】生成失败，资产ID：{}", id);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "生成二维码失败");
            return;
        }

        ImageIO.write(image, "PNG", response.getOutputStream());
        response.getOutputStream().flush();
        log.info("【资产台账-二维码预览】成功，资产ID：{}，资产编码：{}", id, asset.getAssetCode());
    }

    /**
     * 下载单个二维码（PNG格式）
     */
    @RequiresPermissions("asset:assets:query")
    @GetMapping("/qrcode/download/{id}")
    public void downloadQrCode(@PathVariable Long id, HttpServletResponse response) throws Exception {
        log.info("【资产台账-二维码下载】开始，资产ID：{}", id);

        Assets asset = assetsService.getById(id);
        if (asset == null) {
            log.error("【资产台账-二维码下载】资产不存在，ID：{}", id);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "资产不存在");
            return;
        }

        response.setContentType("image/png");
        response.setHeader("Content-Disposition", "attachment;filename=" + asset.getAssetCode() + ".png");

        String baseUrl = "http://localhost:80";
        String qrContent = baseUrl + "/asset/assets/view?id=" + asset.getId();

        BufferedImage image = ZxingUtils.createQRCode(qrContent, 350, 350);
        if (image == null) {
            log.error("【资产台账-二维码下载】生成失败，资产ID：{}", id);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "生成二维码失败");
            return;
        }

        ImageIO.write(image, "PNG", response.getOutputStream());
        response.getOutputStream().flush();
        log.info("【资产台账-二维码下载】成功，资产ID：{}，资产编码：{}", id, asset.getAssetCode());
    }

    //  条码相关接口 

    /**
     * 预览条码（图片直接在浏览器显示）
     */
    @RequiresPermissions("asset:assets:query")
    @GetMapping("/barcode/preview/{assetCode}")
    public void previewBarCode(@PathVariable String assetCode, HttpServletResponse response) throws Exception {
        log.info("【资产台账-条码预览】开始，资产编码：{}", assetCode);
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");

        Assets asset = assetsService.selectByAssetCode(assetCode);
        String barcodeContent = asset != null && asset.getId() != null
                ? asset.getId() + "|" + assetCode
                : assetCode;

        BufferedImage image = ZxingUtils.createBarCode(barcodeContent, 300, 100);
        if (image == null) {
            log.error("【资产台账-条码预览】生成失败，资产编码：{}", assetCode);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "生成条码失败");
            return;
        }

        ImageIO.write(image, "PNG", response.getOutputStream());
        response.getOutputStream().flush();
        log.info("【资产台账-条码预览】成功，资产编码：{}", assetCode);
    }

    /**
     * 下载单个条码（PNG格式）
     */
    @RequiresPermissions("asset:assets:query")
    @PostMapping("/barcode/download/{assetCode}")
    public void downloadBarCode(@PathVariable String assetCode, HttpServletResponse response) throws Exception {
        log.info("【资产台账-条码下载】开始，资产编码：{}", assetCode);

        response.setContentType("image/png");
        response.setHeader("Content-Disposition", "attachment;filename=" + assetCode + ".png");

        Assets asset = assetsService.selectByAssetCode(assetCode);
        String barcodeContent = asset != null && asset.getId() != null
                ? asset.getId() + "|" + assetCode
                : assetCode;

        BufferedImage image = ZxingUtils.createBarCode(barcodeContent, 300, 100);
        if (image == null) {
            log.error("【资产台账-条码下载】生成失败，资产编码：{}", assetCode);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "生成条码失败");
            return;
        }

        ImageIO.write(image, "PNG", response.getOutputStream());
        response.getOutputStream().flush();
        log.info("【资产台账-条码下载】成功，资产编码：{}", assetCode);
    }
}