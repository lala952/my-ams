package com.ruoyi.asset.utils;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 字体工具类
 * 用于获取resources下的字体
 */
public class FontUtils {
    /**
     * 创建楷体字体
     * @return pdfFont
     * @throws IOException
     */
    public static PdfFont createKaiFont() throws IOException {
        try (InputStream fontStream = FontUtils.class.getResourceAsStream("/fonts/simkai.ttf")) {
            if (fontStream == null) {
                throw new RuntimeException("字体文件不存在，请将字体文件放在 src/main/resources/fonts/ 目录下");
            }
            byte[] fontData = IOUtils.toByteArray(fontStream);
            return PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
        }
    }

    /**
     * 创建仿宋字体
     * @return pdfFont
     * @throws IOException
     */
    public static PdfFont createFangsongFont() throws IOException {
        try (InputStream fontStream = FontUtils.class.getResourceAsStream("/fonts/simfang.ttf")) {
            if (fontStream == null) {
                throw new RuntimeException("字体文件不存在，请将字体文件放在 src/main/resources/fonts/ 目录下");
            }
            byte[] fontData = IOUtils.toByteArray(fontStream);
            return PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
        }
    }

    /**
     * 创建黑体字体
     * @return pdfFont
     * @throws IOException
     */
    public static PdfFont createHeiFont() throws IOException {
        try (InputStream fontStream = FontUtils.class.getResourceAsStream("/fonts/simhei.ttf")) {
            if (fontStream == null) {
                throw new RuntimeException("字体文件不存在，请将字体文件放在 src/main/resources/fonts/ 目录下");
            }
            byte[] fontData = IOUtils.toByteArray(fontStream);
            return PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
        }
    }
}
