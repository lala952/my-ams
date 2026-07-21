package com.ruoyi.asset.utils;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * 字体工具类 - 缓存字体，避免重复加载
 */
public class FontUtils {

    private static final Logger log = LoggerFactory.getLogger(FontUtils.class);

    // 字体缓存（单例）
    private static PdfFont kaiFont;
    private static PdfFont fangsongFont;
    private static PdfFont heiFont;

    /**
     * 获取楷体字体（缓存）
     */
    public static synchronized PdfFont createKaiFont() throws IOException {
        if (kaiFont == null) {
            log.info("加载楷体字体...");
            try (InputStream fontStream = FontUtils.class.getResourceAsStream("/fonts/simkai.ttf")) {
                if (fontStream == null) {
                    throw new RuntimeException("楷体字体文件不存在，请将 simkai.ttf 放在 src/main/resources/fonts/ 目录下");
                }
                byte[] fontData = IOUtils.toByteArray(fontStream);
                kaiFont = PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
                log.info("楷体字体加载完成");
            }
        }
        return kaiFont;
    }

    /**
     * 获取仿宋字体（缓存）
     */
    public static synchronized PdfFont createFangsongFont() throws IOException {
        if (fangsongFont == null) {
            log.info("加载仿宋字体...");
            try (InputStream fontStream = FontUtils.class.getResourceAsStream("/fonts/simfang.ttf")) {
                if (fontStream == null) {
                    throw new RuntimeException("仿宋字体文件不存在，请将 simfang.ttf 放在 src/main/resources/fonts/ 目录下");
                }
                byte[] fontData = IOUtils.toByteArray(fontStream);
                fangsongFont = PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
                log.info("仿宋字体加载完成");
            }
        }
        return fangsongFont;
    }

    /**
     * 获取黑体字体（缓存）
     */
    public static synchronized PdfFont createHeiFont() throws IOException {
        if (heiFont == null) {
            log.info("加载黑体字体...");
            try (InputStream fontStream = FontUtils.class.getResourceAsStream("/fonts/simhei.ttf")) {
                if (fontStream == null) {
                    throw new RuntimeException("黑体字体文件不存在，请将 simhei.ttf 放在 src/main/resources/fonts/ 目录下");
                }
                byte[] fontData = IOUtils.toByteArray(fontStream);
                heiFont = PdfFontFactory.createFont(fontData, PdfEncodings.IDENTITY_H);
                log.info("黑体字体加载完成");
            }
        }
        return heiFont;
    }

    /**
     * 清除字体缓存（用于测试或内存紧张时）
     */
    public static synchronized void clearCache() {
        kaiFont = null;
        fangsongFont = null;
        heiFont = null;
        log.info("字体缓存已清除");
    }
}