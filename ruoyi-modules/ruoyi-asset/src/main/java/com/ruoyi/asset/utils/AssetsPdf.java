package com.ruoyi.asset.utils;

import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.ruoyi.asset.constant.DictConstants;
import com.ruoyi.asset.domain.Assets;
import com.ruoyi.common.core.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 资产PDF导出工具类
 * 用于生成资产信息的PDF文档，包含资产基本信息、财务信息、使用信息、二维码和条码等
 *
 * @author ruoyi
 */
@Component
public class AssetsPdf {

    private static final Logger log = LoggerFactory.getLogger(AssetsPdf.class);

    /**
     * 单页条码数量（每页 4列 × 5行 = 20个）
     */
    private static final int BARCODES_PER_PAGE = 20;

    /**
     * 每列宽度百分比
     */
    private static final float[] COLUMN_WIDTHS = {1, 1, 1, 1};

    /**
     * 导出资产信息PDF文件
     * 根据资产信息生成完整的PDF文档，包含标题、二维码、条码、基本信息表、财务信息表、使用信息表和水印
     *
     * @param response HttpServletResponse对象，用于输出PDF文件
     * @param assets   资产信息对象
     * @throws IOException 文件输出时可能发生的IO异常
     */
    public void exportPdf(HttpServletResponse response, Assets assets) throws IOException {
        // 1.设置响应头
        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");

        // 2.创建PDF文档（使用A4横向）
        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4.rotate())) {

            // 3.设置文档边距：上20、右15、下20、左15
            document.setMargins(20, 15, 20, 15);

            // 4.创建字体（使用缓存）
            PdfFont fangsongFont = FontUtils.createFangsongFont();
            PdfFont kaiFont = FontUtils.createKaiFont();
            PdfFont heiFont = FontUtils.createHeiFont();

            // 5.添加主标题、二维码和条码
            PdfUtils.addMainTitle(assets.getAssetName(), document, heiFont);
            // 5.1 构建二维码内容并生成二维码
            String qrContent = "asset_code:" + assets.getAssetCode() + "\n"
                    + "asset_name:" + (assets.getAssetName() != null ? assets.getAssetName() : "暂无资产名称");
            PdfUtils.addQrCodeToTopLeft(qrContent, pdf, document);
            // 5.2 生成条码
            PdfUtils.addBarcodeToTopRight(assets.getAssetCode(), pdf, document);

            // 6.添加基本信息章节和表格
            PdfUtils.addSectionTitle(document, heiFont, "基本信息");
            document.add(createBasicInfoTable(assets, kaiFont));

            // 7.添加财务信息章节和表格
            PdfUtils.addSectionTitle(document, heiFont, "财务信息");
            document.add(createFinanceTable(assets, kaiFont));

            // 8.添加使用信息章节和表格
            PdfUtils.addSectionTitle(document, heiFont, "使用信息");
            document.add(createUsageTable(assets, kaiFont));

            // 9.添加文字水印
            PdfUtils.addWatermark(pdf, "资产管理系统", fangsongFont);

            log.info("资产PDF生成成功，资产ID：{}，资产编码：{}", assets.getId(), assets.getAssetCode());
        } catch (Exception e) {
            log.error("生成资产PDF失败", e);
            throw new IOException("生成PDF失败：" + e.getMessage(), e);
        }
    }

    /**
     * 创建基本信息表格
     *
     * @param assets 资产信息对象
     * @param font   使用的字体
     * @return 基本信息表格对象
     */
    private Table createBasicInfoTable(Assets assets, PdfFont font) {
        // 1.创建8列表格，列宽比例为1:2:1:2:1:2
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2, 1, 2, 1, 2, 1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        // 2.第一行：资产编码、资产名称、资产分类
        PdfUtils.addRow8(table, font,
                "资产编码", assets.getAssetCode(),
                "资产名称", assets.getAssetName(),
                "资产分类", "分类" + assets.getCategoryId(),
                "资产类型", DictLabelUtils.getDictLabel(DictConstants.ASSET_TYPE, assets.getAssetType(), "-")
        );

        // 第二行：资产类型、资产来源、资产状态
        PdfUtils.addRow8(table, font,
                "资产来源", DictLabelUtils.getDictLabel(DictConstants.ASSET_SOURCE, assets.getAssetSource(), "-"),
                "资产状态", DictLabelUtils.getDictLabel(DictConstants.ASSET_STATUS, assets.getAssetStatus(), "-"),
                "规格型号", assets.getModel(),
                "品牌", assets.getBrand());

        // 第三行：序列号
        PdfUtils.addCellWithColspan(table, font,
                "供应商","供应商" + assets.getSupplierId(), 3);
        PdfUtils.addCellWithColspan(table, font,
                "序列号", assets.getSerialNumber(),3);

        // 第四行存放位置（跨5列）
        PdfUtils.addCellWithColspan(table, font, "存放位置", assets.getLocation(), 7);

        return table;
    }

    /**
     * 创建财务信息表格
     *
     * @param assets 资产信息对象
     * @param font   使用的字体
     * @return 财务信息表格对象
     */
    private Table createFinanceTable(Assets assets, PdfFont font) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1.2f, 2, 1.2f, 2, 1.2f, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        PdfUtils.addRow6(table, font,
                "原值", CommonUtils.formatMoney(assets.getOriginalValue()),
                "采购价格", CommonUtils.formatMoney(assets.getPurchasePrice()),
                "残值", CommonUtils.formatMoney(assets.getSalvageValue()));

        PdfUtils.addRow6(table, font,
                "累计折旧", CommonUtils.formatMoney(assets.getAccumulatedDepreciation()),
                "净值", CommonUtils.formatMoney(assets.getNetValue()),
                "折旧方法", DictLabelUtils.getDictLabel(DictConstants.DEPRECIATION_METHOD, assets.getDepreciationMethod(), "-"));

        PdfUtils.addRow6(table, font,
                "折旧率(%)", assets.getDepreciationRate() != null ? assets.getDepreciationRate().toString() : "-",
                "购置日期",  DateUtils.parseDateToStr("yyyy-MM-dd", assets.getPurchaseDate()),
                "开始折旧日期", DateUtils.parseDateToStr("yyyy-MM-dd", assets.getDepreciationStartDate()));
        return table;
    }

    /**
     * 创建使用信息表格
     *
     * @param assets 资产信息对象
     * @param font   使用的字体
     * @return 使用信息表格对象
     */
    private Table createUsageTable(Assets assets, PdfFont font) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1.2f, 2, 1.2f, 2, 1.2f, 2,1.2f,2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        PdfUtils.addRow8(table, font,
                "数量", assets.getQuantity() != null ? assets.getQuantity().toString() : "0",
                "单位", DictLabelUtils.getDictLabel(DictConstants.MEASURE_UNIT, assets.getUnit()),
                "使用人", "用户" + assets.getUserId(),
                "使用部门", "部门" + assets.getDeptId());

        PdfUtils.addRow8(table, font,
                "开始使用日期", assets.getStartUseDate() != null ? DateUtils.parseDateToStr("yyyy-MM-dd", assets.getStartUseDate()) :  "-",
                "生产日期",assets.getStartUseDate() != null ? DateUtils.parseDateToStr("yyyy-MM-dd", assets.getManufactureDate()) :  "-",
                "保修截止日期",assets.getWarrantyExpireDate() != null ? DateUtils.parseDateToStr("yyyy-MM-dd", assets.getWarrantyExpireDate()) :  "-",
                "报废日期",assets.getScrapDate() != null ? DateUtils.parseDateToStr("yyyy-MM-dd", assets.getScrapDate()) :  "-");

        PdfUtils.addCellWithColspan(table, font, "RFID标签", assets.getRfidTag(), 7);
        PdfUtils.addCellWithColspan(table, font, "备注", assets.getRemark(), 7);

        return table;
    }

    /**
     * 导出资产条码PDF文件（批量）- 流式分页处理
     * 每页最多 20 个条码，避免一次性加载所有数据导致 OOM
     *
     * @param response   HttpServletResponse对象，用于输出PDF文件
     * @param assetsList 资产列表
     * @throws IOException 文件输出时可能发生的IO异常
     */
    public void exportBarcodePdf(HttpServletResponse response, List<Assets> assetsList) throws IOException {
        // 1. 校验空列表
        if (assetsList == null || assetsList.isEmpty()) {
            log.warn("导出条码PDF失败：资产列表为空");
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write("{\"code\":500,\"msg\":\"没有可导出的资产数据\"}");
            return;
        }

        int totalSize = assetsList.size();
        int totalPages = (int) Math.ceil((double) totalSize / BARCODES_PER_PAGE);
        log.info("开始导出条码PDF，共 {} 条资产，分 {} 页", totalSize, totalPages);

        // 2. 设置响应头
        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=barcodes.pdf");

        // 3. 创建PDF文档
        try (PdfWriter writer = new PdfWriter(response.getOutputStream())) {

            // 4. 复用字体（单例缓存）
            PdfFont kaiFont = FontUtils.createKaiFont();
            PdfFont heiFont = FontUtils.createHeiFont();

            // 5. 分页处理
            for (int page = 0; page < totalPages; page++) {
                int start = page * BARCODES_PER_PAGE;
                int end = Math.min(start + BARCODES_PER_PAGE, totalSize);

                // 创建新页面
                try (PdfDocument pdf = new PdfDocument(writer);
                     Document document = new Document(pdf, PageSize.A4)) {

                    // 如果不是第一页，需要告知 writer 创建新页面
                    if (page > 0) {
                        pdf.addNewPage();
                    }

                    document.setMargins(15, 15, 15, 15);

                    // 添加标题（每页都有）
                    PdfUtils.addMainTitle("资产条码", document, heiFont);

                    // 创建4列表格
                    Table table = new Table(UnitValue.createPercentArray(COLUMN_WIDTHS))
                            .useAllAvailableWidth();
                    table.setKeepTogether(false);

                    // 处理当前页的资产
                    for (int i = start; i < end; i++) {
                        Assets asset = assetsList.get(i);
                        Cell cell = new Cell()
                                .add(new Paragraph(asset.getAssetName() != null ? asset.getAssetName() : "")
                                        .setFont(kaiFont)
                                        .setFontSize(8)
                                        .setTextAlignment(TextAlignment.CENTER)
                                        .setMarginBottom(5))
                                .add(createBarcodeImage(asset, pdf))
                                .setPadding(8)
                                .setBorder(null);
                        table.addCell(cell);
                    }

                    // 补齐剩余单元格（保持每行4个）
                    int count = end - start;
                    while (count % 4 != 0) {
                        table.addCell(new Cell().setBorder(null));
                        count++;
                    }

                    document.add(table);
                    document.close();

                    log.debug("第 {} 页条码PDF生成完成，包含 {} 条", page + 1, end - start);
                }
            }

            log.info("条码PDF生成成功，共 {} 条资产，分 {} 页", totalSize, totalPages);

        } catch (Exception e) {
            log.error("生成资产条码PDF失败", e);
            throw new IOException("生成条码PDF失败：" + e.getMessage(), e);
        }
    }

    /**
     * 创建条码图片
     *
     * @param asset 资产信息对象
     * @param pdf   PDF文档对象
     * @return 条码图片对象
     */
    private Image createBarcodeImage(Assets asset, PdfDocument pdf) {
        // 生成128条码
        Barcode128 barcode = new Barcode128(pdf);
        barcode.setCode(asset.getAssetCode() != null ? asset.getAssetCode() : "");
        barcode.setCodeType(Barcode128.CODE128);
        PdfFormXObject barcodeXObject = barcode.createFormXObject(pdf);

        // 创建条码图片并设置尺寸
        Image barcodeImage = new Image(barcodeXObject);
        barcodeImage.setWidth(120);
        barcodeImage.setHeight(45);
        barcodeImage.setMarginBottom(3);

        return barcodeImage;
    }
}