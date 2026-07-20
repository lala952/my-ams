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

    @Autowired
    private UserUtils userUtils;

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

            // 4.创建字体（普通字体和黑体）
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
     * 展示资产的基本信息，包括资产编码、名称、分类、类型、来源、状态、规格、品牌、序列号、存放位置等
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
     * 展示资产的财务信息，包括原值、采购价格、残值、累计折旧、净值、折旧方法、折旧率、购置日期、开始折旧日期等
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
     * 展示资产的使用信息，包括数量、单位、使用部门、使用人、开始使用日期、生产日期、保修截止日期、报废日期、条码、RFID标签、备注等
     *
     * @param assets 资产信息对象
     * @param font   使用的字体
     * @return 使用信息表格对象
     */
    private Table createUsageTable(Assets assets, PdfFont font) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1.2f, 2, 1.2f, 2, 1.2f, 2,1.2f,2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        // 数量、单位、使用部门 todo 使用人和使用部门调用接口出错，所以这里写死
        PdfUtils.addRow8(table, font,
                "数量", assets.getQuantity() != null ? assets.getQuantity().toString() : "0",
                "单位", DictLabelUtils.getDictLabel(DictConstants.MEASURE_UNIT, assets.getUnit()),
                "使用人", "用户" + assets.getUserId(),
                "使用部门", "部门" + assets.getDeptId());

        // 使用人、开始使用日期、生产日期
        PdfUtils.addRow8(table, font,
                "开始使用日期", assets.getStartUseDate() != null ? DateUtils.parseDateToStr("yyyy-MM-dd", assets.getStartUseDate()) :  "-",
                "生产日期",assets.getStartUseDate() != null ? DateUtils.parseDateToStr("yyyy-MM-dd", assets.getManufactureDate()) :  "-",
                "保修截止日期",assets.getWarrantyExpireDate() != null ? DateUtils.parseDateToStr("yyyy-MM-dd", assets.getWarrantyExpireDate()) :  "-",
                "报废日期",assets.getScrapDate() != null ? DateUtils.parseDateToStr("yyyy-MM-dd", assets.getScrapDate()) :  "-");

        // RFID标签
        PdfUtils.addCellWithColspan(table, font, "RFID标签", assets.getRfidTag(), 7);
        // 备注
        PdfUtils.addCellWithColspan(table, font, "备注", assets.getRemark(), 7);

        return table;
    }

    /**
     * 导出资产条码PDF文件（批量）
     * 以网格形式展示多个资产的条码，每行4个，便于打印和粘贴
     *
     * @param response   HttpServletResponse对象，用于输出PDF文件
     * @param assetsList 资产列表
     * @throws IOException 文件输出时可能发生的IO异常
     */
    public void exportBarcodePdf(HttpServletResponse response, List<Assets> assetsList) throws IOException {
        // 1.设置响应头
        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");

        // 2.创建PDF文档（使用A4竖版）
        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {

            // 3.设置文档边距
            document.setMargins(15, 15, 15, 15);

            // 4.创建字体
            PdfFont kaiFont = FontUtils.createKaiFont();
            PdfFont HeiFont = FontUtils.createHeiFont();

            // 5.添加主标题
            PdfUtils.addMainTitle("资产条码", document, HeiFont);

            // 6.创建4列表格用于放置条码
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1}))
                    .useAllAvailableWidth();
            table.setKeepTogether(false);

            // 7.遍历资产列表，为每个资产创建条码单元格
            int count = 0;
            for (Assets asset : assetsList) {
                // 创建单元格：包含资产名称和条码图片
                Cell cell = new Cell()
                        .add(new Paragraph(asset.getAssetName())
                                .setFont(kaiFont)
                                .setFontSize(8)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setMarginBottom(5))
                        .add(createBarcodeImage(asset, pdf))
                        .setPadding(8)
                        .setBorder(null); // 无边框
                table.addCell(cell);
                count++;
            }

            // 8.补齐剩余单元格，保持每行4个
            while (count % 4 != 0) {
                table.addCell(new Cell().setBorder(null));
                count++;
            }

            document.add(table);

            log.info("资产条码PDF生成成功，共{}条", assetsList.size());
        } catch (Exception e) {
            log.error("生成资产条码PDF失败", e);
            throw new IOException("生成条码PDF失败：" + e.getMessage(), e);
        }
    }

    /**
     * 创建条码图片
     * 根据资产信息生成128条码图片
     *
     * @param asset 资产信息对象
     * @param pdf   PDF文档对象
     * @return 条码图片对象
     */
    private Image createBarcodeImage(Assets asset, PdfDocument pdf) {
        // 生成128条码
        Barcode128 barcode = new Barcode128(pdf);
        barcode.setCode(asset.getAssetCode());
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