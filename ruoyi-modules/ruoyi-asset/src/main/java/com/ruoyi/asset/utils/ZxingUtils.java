package com.ruoyi.asset.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.oned.Code128Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码和条码工具类（基于 ZXing）
 *
 * @author xiaowang
 * @date 2026-05-22
 */
public class ZxingUtils {

    private static final Logger log = LoggerFactory.getLogger(ZxingUtils.class);

    /** 默认二维码宽度 */
    private static final int DEFAULT_QR_WIDTH = 300;

    /** 默认二维码高度 */
    private static final int DEFAULT_QR_HEIGHT = 300;

    /** 默认条码宽度 */
    private static final int DEFAULT_BARCODE_WIDTH = 300;

    /** 默认条码高度 */
    private static final int DEFAULT_BARCODE_HEIGHT = 100;

    /** 默认图片格式 */
    private static final String IMAGE_FORMAT = "PNG";

    //  二维码相关 

    /**
     * 生成二维码图片（纯二维码，不带文字）
     *
     * @param content 二维码内容（如：http://localhost/asset/assets/view?id=100）
     * @return BufferedImage 二维码图片
     */
    public static BufferedImage createQRCode(String content) {
        return createQRCode(content, DEFAULT_QR_WIDTH, DEFAULT_QR_HEIGHT);
    }

    /**
     * 生成二维码图片（纯二维码，不带文字）
     *
     * @param content 二维码内容
     * @param width   宽度（像素）
     * @param height  高度（像素）
     * @return BufferedImage 二维码图片
     */
    public static BufferedImage createQRCode(String content, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            log.error("生成二维码失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 生成带文字的二维码
     *
     * @param content      二维码内容
     * @param width        二维码宽度
     * @param totalHeight  总高度（包含文字区域）
     * @param text         下方文字
     * @return BufferedImage 带文字的二维码图片
     */
    public static BufferedImage createQRCodeWithText(String content, int width, int totalHeight, String text) {
        BufferedImage qrImage = createQRCode(content, width, width);
        if (qrImage == null) {
            return null;
        }

        // 文字高度
        int textHeight = 30;
        int qrHeight = width;

        // 创建带文字的新图片
        BufferedImage combined = new BufferedImage(width, qrHeight + textHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = combined.createGraphics();

        // 白色背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, qrHeight + textHeight);

        // 绘制二维码
        g.drawImage(qrImage, 0, 0, null);

        // 绘制文字
        g.setColor(Color.BLACK);
        g.setFont(new Font("宋体", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (width - textWidth) / 2;
        int y = qrHeight + 20;
        g.drawString(text, x, y);

        g.dispose();
        return combined;
    }

    /**
     * 生成二维码并返回字节数组（用于下载）
     *
     * @param content 二维码内容
     * @return byte[] 图片字节数组
     */
    public static byte[] createQRCodeBytes(String content) {
        BufferedImage image = createQRCode(content);
        return imageToBytes(image);
    }

    /**
     * 生成二维码并返回Base64（用于前端展示）
     *
     * @param content 二维码内容
     * @return String Base64编码的图片
     */
    public static String createQRCodeBase64(String content) {
        BufferedImage image = createQRCode(content);
        return imageToBase64(image);
    }

    //  条码相关 

    /**
     * 生成Code128条码图片（纯条码）
     *
     * @param content 条码内容（建议只包含数字和字母）
     * @return BufferedImage 条码图片
     */
    public static BufferedImage createBarCode(String content) {
        return createBarCode(content, DEFAULT_BARCODE_WIDTH, DEFAULT_BARCODE_HEIGHT);
    }

    /**
     * 生成Code128条码图片（纯条码）
     *
     * @param content 条码内容
     * @param width   宽度
     * @param height  高度
     * @return BufferedImage 条码图片
     */
    public static BufferedImage createBarCode(String content, int width, int height) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 2);

        Code128Writer writer = new Code128Writer();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.CODE_128, width, height, hints);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    /**
     * 生成带文字的条码
     *
     * @param content      条码内容
     * @param width        条码宽度
     * @param totalHeight  总高度（包含文字区域）
     * @return BufferedImage 带文字的条码图片
     */
    public static BufferedImage createBarCodeWithText(String content, int width, int totalHeight) {
        BufferedImage barImage = createBarCode(content, width, totalHeight - 25);
        if (barImage == null) {
            return null;
        }

        int textHeight = 25;
        int barHeight = totalHeight - textHeight;

        BufferedImage combined = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = combined.createGraphics();

        // 白色背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, totalHeight);

        // 绘制条码
        g.drawImage(barImage, 0, 0, null);

        // 绘制文字
        g.setColor(Color.BLACK);
        g.setFont(new Font("宋体", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(content);
        int x = (width - textWidth) / 2;
        int y = barHeight + 18;
        g.drawString(content, x, y);

        g.dispose();
        return combined;
    }

    /**
     * 生成条码并返回字节数组（用于下载）
     *
     * @param content 条码内容
     * @return byte[] 图片字节数组
     */
    public static byte[] createBarCodeBytes(String content) {
        BufferedImage image = createBarCode(content);
        return imageToBytes(image);
    }

    /**
     * 生成条码并返回Base64（用于前端展示）
     *
     * @param content 条码内容
     * @return String Base64编码的图片
     */
    public static String createBarCodeBase64(String content) {
        BufferedImage image = createBarCode(content);
        return imageToBase64(image);
    }

    //  工具方法 

    /**
     * BufferedImage 转 byte[]
     *
     * @param image BufferedImage
     * @return byte[]
     */
    private static byte[] imageToBytes(BufferedImage image) {
        if (image == null) {
            return new byte[0];
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, IMAGE_FORMAT, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("图片转字节数组失败: {}", e.getMessage());
            return new byte[0];
        }
    }

    /**
     * BufferedImage 转 Base64
     *
     * @param image BufferedImage
     * @return String Base64编码的图片
     */
    private static String imageToBase64(BufferedImage image) {
        byte[] bytes = imageToBytes(image);
        if (bytes.length == 0) {
            return "";
        }
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 生成带文字和边框的二维码（用于打印标签）
     *
     * @param content      二维码内容
     * @param title        标题（如资产名称）
     * @param subTitle     副标题（如资产编码）
     * @return BufferedImage 标签图片
     */
    public static BufferedImage createQrCodeLabel(String content, String title, String subTitle) {
        int width = 280;
        int qrSize = 200;
        int titleHeight = 40;
        int subTitleHeight = 30;
        int totalHeight = titleHeight + qrSize + subTitleHeight + 20;

        BufferedImage qrImage = createQRCode(content, qrSize, qrSize);
        if (qrImage == null) {
            return null;
        }

        BufferedImage combined = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = combined.createGraphics();

        // 白色背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, totalHeight);

        // 绘制边框
        g.setColor(Color.BLACK);
        g.drawRect(1, 1, width - 2, totalHeight - 2);

        // 绘制标题
        g.setColor(Color.BLACK);
        g.setFont(new Font("宋体", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int x = (width - titleWidth) / 2;
        g.drawString(title, x, 25);

        // 绘制二维码
        int qrX = (width - qrSize) / 2;
        g.drawImage(qrImage, qrX, titleHeight, null);

        // 绘制副标题
        g.setFont(new Font("宋体", Font.PLAIN, 11));
        fm = g.getFontMetrics();
        int subWidth = fm.stringWidth(subTitle);
        x = (width - subWidth) / 2;
        g.drawString(subTitle, x, titleHeight + qrSize + 20);

        g.dispose();
        return combined;
    }
}