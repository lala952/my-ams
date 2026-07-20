package com.ruoyi.asset.utils;

import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;

import javax.servlet.http.HttpServletRequest;

public class PdfUtils {

    // 文档标题字体大小
    public static final int DOCUMENT_TITLE_FONT_SIZE = 18;
    // 章节标题字体大小
    public static final int TITLE_FONT_SIZE = 12;
    // 正文字体大小
    public static final int CONTENT_FONT_SIZE = 10;
    // 水印字体大小
    public static final int WATERMARK_TEXT_FONT_SIZE = 30;
    // 签名图片宽度
    public static final float SIGNATURE_IMAGE_WIDTH = 100f;

    /**
     * 读取PDF文件
     * 从HttpServletRequest中读取PDF输入流并创建PDF文档对象
     *
     * @param request HttpServletRequest对象，包含PDF文件输入流
     * @throws Exception 读取PDF时可能发生的异常
     */
    public void readPdf(HttpServletRequest request) throws Exception {
        // 1.创建PDF文档（使用A4横向）
        try (PdfReader reader = new PdfReader(request.getInputStream()); PdfDocument pdf = new PdfDocument(reader); Document document = new Document(pdf, PageSize.A4.rotate())) {
            // 2.设置文档边距：上20、右15、下20、左15
            document.setMargins(20, 15, 20, 15);
        }
        // TODO 读取pdf的一些业务后续再处理
    }


    /**
     * 添加主标题
     *
     * @param title    标题文本
     * @param document PDF文档对象
     * @param font     字体（黑体）
     */
    public static void addMainTitle(String title, Document document, PdfFont font) {
        Paragraph mainTitle = new Paragraph(title).setFont(font).setFontSize(DOCUMENT_TITLE_FONT_SIZE).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20);
        document.add(mainTitle);
    }

    /**
     * 添加章节标题
     *
     * @param document     PDF文档对象
     * @param font         字体（黑体）
     * @param sectionTitle 章节标题文本
     */
    public static void addSectionTitle(Document document, PdfFont font, String sectionTitle) {
        Paragraph title = new Paragraph(sectionTitle).setFont(font).setFontSize(TITLE_FONT_SIZE).setMarginBottom(10);
        document.add(title);
    }

    /**
     * 添加二维码至页面左上角
     * 二维码内容包含变动单编码和变动单类型
     * 位置与页面左边距和上边距保持一致
     *
     * @param qrContent 二维码内容
     * @param pdf       PDF文档对象
     * @param document  PDF文档对象（用于获取边距）
     */
    public static void addQrCodeToTopLeft(String qrContent, PdfDocument pdf, Document document) {
        // 1.生成二维码
        BarcodeQRCode qrCode = new BarcodeQRCode(qrContent);
        PdfFormXObject qrXObject = qrCode.createFormXObject(pdf);

        // 2.计算二维码位置：X轴为左边距，Y轴为页面高度减去二维码高度再减去上边距
        float x = document.getLeftMargin();
        float y = pdf.getDefaultPageSize().getHeight() - qrXObject.getHeight() - document.getTopMargin();

        // 3.在页面左上角绘制二维码
        PdfCanvas pdfCanvas = new PdfCanvas(pdf.getFirstPage().newContentStreamBefore(), pdf.getFirstPage().getResources(), pdf);
        pdfCanvas.addXObjectAt(qrXObject, x, y);
        pdfCanvas.release();
    }

    /**
     * 添加条码至页面右上角
     * 条码内容为变动单编码
     * 位置与页面右边距和上边距保持一致
     *
     * @param code     编码
     * @param pdf      PDF文档对象
     * @param document PDF文档对象（用于获取边距）
     */
    public static void addBarcodeToTopRight(String code, PdfDocument pdf, Document document) {
        // 1.生成128条码
        Barcode128 barcode = new Barcode128(pdf);
        barcode.setCode(code);
        barcode.setCodeType(Barcode128.CODE128);
        PdfFormXObject barcodeXObject = barcode.createFormXObject(pdf);

        // 3.获取条码实际宽高
        float barcodeWidth = barcodeXObject.getWidth();
        float barcodeHeight = barcodeXObject.getHeight();

        // 4.计算条码位置：X轴为页面宽度减去右边距再减条码宽度，Y轴为页面高度减去上边距再减条码高度
        float x = pdf.getDefaultPageSize().getWidth() - document.getRightMargin() - barcodeWidth;
        float y = pdf.getDefaultPageSize().getHeight() - document.getTopMargin() - barcodeHeight;

        // 5.在页面右上角绘制条码
        PdfCanvas pdfCanvas = new PdfCanvas(pdf.getFirstPage().newContentStreamBefore(), pdf.getFirstPage().getResources(), pdf);
        pdfCanvas.addXObjectAt(barcodeXObject, x, y);
        pdfCanvas.release();
    }

    /**
     * 添加一行6列信息（标签-值-标签-值-标签-值）
     *
     * @param table  表格对象
     * @param font   字体
     * @param label1 第1列标签
     * @param value1 第2列值
     * @param label2 第3列标签
     * @param value2 第4列值
     * @param label3 第5列标签
     * @param value3 第6列值
     */
    public static void addRow6(Table table, PdfFont font, String label1, String value1, String label2, String value2, String label3, String value3) {
        addLabelCell(table, font, label1);
        addValueCell(table, font, value1);
        addLabelCell(table, font, label2);
        addValueCell(table, font, value2);
        addLabelCell(table, font, label3);
        addValueCell(table, font, value3);
    }

    /**
     * 添加一行8列信息（标签-值-标签-值-标签-值）
     *
     * @param table  表格对象
     * @param font   字体
     * @param label1 第1列标签
     * @param value1 第2列值
     * @param label2 第3列标签
     * @param value2 第4列值
     * @param label3 第5列标签
     * @param value3 第6列值
     * @param label4 第7列标签
     * @param value4 第8列值
     */
    public static void addRow8(Table table, PdfFont font, String label1, String value1, String label2, String value2, String label3, String value3, String label4, String value4) {
        addLabelCell(table, font, label1);
        addValueCell(table, font, value1);
        addLabelCell(table, font, label2);
        addValueCell(table, font, value2);
        addLabelCell(table, font, label3);
        addValueCell(table, font, value3);
        addLabelCell(table, font, label4);
        addValueCell(table, font, value4);
    }

    /**
     * 添加跨列表格行（标签列 + 跨列值）
     *
     * @param table   表格对象
     * @param font    字体
     * @param label   标签文本
     * @param value   值文本
     * @param colspan 跨列数
     */
    public static void addCellWithColspan(Table table, PdfFont font, String label, String value, int colspan) {
        addLabelCell(table, font, label);
        Cell valueCell = new Cell(1, colspan).add(new Paragraph(value == null || value.isEmpty() ? "-" : value).setFont(font).setFontSize(CONTENT_FONT_SIZE)).setPadding(5);
        table.addCell(valueCell);
    }

    /**
     * 添加跨列表格行（标签列 + 跨列值）
     *
     * @param table   表格对象
     * @param font    字体
     * @param label   标签文本
     * @param value   值文本
     * @param colspan 跨列数
     */
    public static void addCellWithRowspanAndColspan(Table table, PdfFont font, String label, String value, int rowspan, int colspan) {
        addLabelCell(table, font, label);
        Cell valueCell = new Cell(rowspan, colspan).add(new Paragraph(value == null || value.isEmpty() ? "-" : value).setFont(font).setFontSize(CONTENT_FONT_SIZE)).setPadding(5);
        table.addCell(valueCell);
    }

    /**
     * 添加标签单元格（右对齐）
     *
     * @param table 表格对象
     * @param font  字体
     * @param text  标签文本
     */
    public static void addLabelCell(Table table, PdfFont font, String text) {
        table.addCell(new Cell().add(new Paragraph(text).setFont(font).setFontSize(CONTENT_FONT_SIZE)).setTextAlignment(TextAlignment.RIGHT).setPadding(5));
    }

    /**
     * 添加标签单元格（居中）
     *
     * @param table 表格对象
     * @param font  字体
     * @param text  标签文本
     */
    public static void addCenterLabelCell(Table table, PdfFont font, String text) {
        table.addCell(new Cell().add(new Paragraph(text).setFont(font).setFontSize(CONTENT_FONT_SIZE)).setTextAlignment(TextAlignment.CENTER).setPadding(5));
    }

    public static void addLeftLableCell(Table table, PdfFont font, String text) {
        table.addCell(new Cell().add(new Paragraph(text).setFont(font).setFontSize(CONTENT_FONT_SIZE)).setTextAlignment(TextAlignment.LEFT).setPadding(5));
    }

    /**
     * 添加值单元格（左对齐）
     *
     * @param table 表格对象
     * @param font  字体
     * @param text  值文本
     */
    public static void addValueCell(Table table, PdfFont font, String text) {
        table.addCell(new Cell().add(new Paragraph(text == null || text.isEmpty() ? "-" : text).setFont(font).setFontSize(CONTENT_FONT_SIZE)).setPadding(5));
    }

    /**
     * 添加值单元格（居中）
     *
     * @param table 表格对象
     * @param font  字体
     * @param text  值文本
     */
    public static void addCenterValueCell(Table table, PdfFont font, String text) {
        table.addCell(new Cell().add(new Paragraph(text == null || text.isEmpty() ? "-" : text).setFont(font).setFontSize(CONTENT_FONT_SIZE)).setPadding(5)).setTextAlignment(TextAlignment.CENTER);
    }

    /**
     * 添加表头单元格（居中）
     *
     * @param table 表格对象
     * @param font  字体
     * @param text  表头文本
     */
    public static void addHeaderCell(Table table, PdfFont font, String text) {
        table.addCell(new Cell().add(new Paragraph(text).setFont(font).setFontSize(CONTENT_FONT_SIZE)).setTextAlignment(TextAlignment.CENTER).setPadding(5));
    }

    /**
     * 添加普通单元格（左对齐）
     *
     * @param table 表格对象
     * @param font  字体
     * @param text  单元格文本
     */
    public static void addCell(Table table, PdfFont font, String text) {
        table.addCell(new Cell().add(new Paragraph(text).setFont(font).setFontSize(CONTENT_FONT_SIZE)).setPadding(5));
    }

    /**
     * 添加居中单元格
     *
     * @param table 表格对象
     * @param font  字体
     * @param text  单元格文本
     */
    public static void addCenterCell(Table table, PdfFont font, String text) {
        table.addCell(new Cell().add(new Paragraph(text).setFont(font).setFontSize(CONTENT_FONT_SIZE)).setTextAlignment(TextAlignment.CENTER).setPadding(5));
    }

    /**
     * 在页面右下角添加审批人签名图片和文字
     *
     * @param document   PDF文档对象
     * @param imagePath  签名图片路径
     * @param pageNumber 页码（1表示第一页，0表示最后一页）
     * @throws Exception 添加签名时可能发生的异常
     */
    public static void addSignatureToBottomRight(Document document, String imagePath, int pageNumber) throws Exception {
        // 1.加载签名图片
        ImageData imageData = ImageDataFactory.create(imagePath);
        Image signatureImage = new Image(imageData);

        // 2.设置图片宽度为100
        float imageWidth = SIGNATURE_IMAGE_WIDTH;
        signatureImage.setWidth(imageWidth);

        // 3.计算图片位置（右下角）
        // 3.1 获取页面尺寸和边距
        PageSize pageSize = document.getPdfDocument().getDefaultPageSize();
        // 3.2 获取页面宽度
        float pageWidth = pageSize.getWidth();
        // 3.3 获取页面右边距
        float marginRight = document.getRightMargin();
        // 3.4 获取页面下边距
        float marginBottom = document.getBottomMargin();

        // 3.5 计算图片位置 = 页面宽度 - 图片宽度 - 页面右边距
        float imageX = pageWidth - imageWidth - marginRight;
        // 3.6 获取图片位置 = 页面下边距
        float imageY = marginBottom;

        // 4.设置图片固定位置
        // 4.1 如果指定了页码，则设置图片固定位置
        if (pageNumber > 0) {
            signatureImage.setFixedPosition(pageNumber, imageX, imageY);
        } else {
            // 4.2 如果未指定页码，则获取文档总页数，将签名放到最后一页，并设置图片固定位置
            int totalPages = document.getPdfDocument().getNumberOfPages();
            // 4.2.1 获取最后一页
            signatureImage.setFixedPosition(totalPages, imageX, imageY);
        }

        // 5.添加图片
        document.add(signatureImage);

        // 6.在图片左侧添加"审批人："文字
        PdfFont font = FontUtils.createHeiFont();
        Paragraph approverText = new Paragraph("审批人：").setFont(font).setFontSize(CONTENT_FONT_SIZE).setMarginBottom(0);

        // 7.计算文字位置
        float textWidth = 80f;
        float textX = imageX - textWidth - 5;
        float textY = imageY + imageWidth * 0.3f;

        // 8.设置文字固定位置
        if (pageNumber > 0) {
            document.showTextAligned(approverText, textX, textY, pageNumber, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        } else {
            int totalPages = document.getPdfDocument().getNumberOfPages();
            document.showTextAligned(approverText, textX, textY, totalPages, TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
        }
    }

    /**
     * 添加文字水印
     * 在PDF每一页添加倾斜的文字水印，透明度0.3，浅灰色
     *
     * @param pdfDocument   PDF文档对象
     * @param watermarkText 水印文字
     * @param font          字体
     */
    public static void addWatermark(PdfDocument pdfDocument, String watermarkText, PdfFont font) {
        // 1.获取当前文档总页数
        int numberOfPages = pdfDocument.getNumberOfPages();

        // 2.水印倾斜角度45度，PI=180°
        double angle = Math.PI / 4;

        // 3.遍历每一页添加水印
        for (int i = 1; i <= numberOfPages; i++) {
            PdfPage page = pdfDocument.getPage(i);
            PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDocument);

            Rectangle pageSize = page.getPageSize();
            float pageWidth = pageSize.getWidth();
            float pageHeight = pageSize.getHeight();

            // 4.保存图形状态
            pdfCanvas.saveState();
            pdfCanvas.setFillColor(ColorConstants.LIGHT_GRAY);
            pdfCanvas.setExtGState(new PdfExtGState().setFillOpacity(0.3f));

            // 5.开始文字渲染
            pdfCanvas.beginText();
            pdfCanvas.setFontAndSize(font, WATERMARK_TEXT_FONT_SIZE);

            // 6.水印间距
            float spacingX = 200;
            float spacingY = 150;

            // 7.平铺水印
            for (float x = -pageWidth; x < pageWidth * 2; x += spacingX) {
                for (float y = -pageHeight; y < pageHeight * 2; y += spacingY) {
                    pdfCanvas.setTextMatrix((float) Math.cos(angle), (float) Math.sin(angle), -(float) Math.sin(angle), (float) Math.cos(angle), x, y);
                    pdfCanvas.showText(watermarkText);
                }
            }

            // 7.结束文字渲染
            pdfCanvas.endText();

            // 8.恢复图形状态，
            pdfCanvas.restoreState(); // 恢复图形状态
        }
    }
}
