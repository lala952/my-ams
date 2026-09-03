package com.ruoyi.asset.utils;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class GenerateCode {

    private static final Logger log = LoggerFactory.getLogger(GenerateCode.class);
    /**
     *  前缀只号允许大写字母,^ 头 $ 尾
     */
    private static final String ALLOWED_PREFIX = "^[A-Z]+$";
    /**
     * 最大序列
     */
    private static final int MAX_SEQ = 99999;
    private static final String DEFAULT_CODE_KEY_PREFIX = "code:";
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 生成业务单号（唯一）
     * 格式：前缀yyyyMMdd0001
     */
    public String generateCode(String codePrefix) {
        // 前缀校验
        if (codePrefix == null || !codePrefix.matches(ALLOWED_PREFIX)) {
            throw new IllegalArgumentException("非法前缀: " + codePrefix + "，仅允许大写字母和下划线");
        }

        try {
            return doGenerateCode(codePrefix);
        } catch (Exception e) {
            log.error("Redis生成单号失败，使用降级方案: {}", e.getMessage());
            return codePrefix + DateUtils.dateTimeNow("yyyyMMddHHmmssSSS");
        }
    }

    private String doGenerateCode(String codePrefix) {
        String date = DateUtils.dateTimeNow("yyyyMMdd");
        String key = DEFAULT_CODE_KEY_PREFIX + codePrefix + ":" + date;
        Long seq = redisTemplate.opsForValue().increment(StrUtil.toLowerCase(key));

        log.debug("【Redis生成单号】key={}, seq={}", key, seq);
        // 首次创建时设置过期时间：到当天结束，确保序号 key 在当天内不会过期，避免生成重复单号
        if (seq != null && seq == 1) {
            long remainSeconds = LocalDateTime.now()
                    .until(LocalDate.now().plusDays(1).atStartOfDay(), ChronoUnit.SECONDS) + 1;
            redisTemplate.expire(key, remainSeconds, DEFAULT_TIME_UNIT);
        }

        // 序号上限校验
        if (seq > MAX_SEQ) {
            throw new RuntimeException("单号已达上限: " + seq);
        }

        // 动态位数：4位或5位
        String seqStr = seq > 9999 ? String.format("%05d", seq) : String.format("%04d", seq);
        // codePrefix + date + seqStr;
        return codePrefix + date + seqStr;
    }

    /**
     * 重置当天的序号（慎用）
     */
    public void resetTodaySeq(String codePrefix) {
        String date = DateUtils.dateTimeNow("yyyyMMdd");
        String key = DEFAULT_CODE_KEY_PREFIX + codePrefix + ":" + date;
        redisTemplate.delete(key);
        log.warn("重置序号: key={}", key);
    }
}