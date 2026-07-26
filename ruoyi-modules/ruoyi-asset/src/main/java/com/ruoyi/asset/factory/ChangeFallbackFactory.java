package com.ruoyi.asset.factory;

import com.ruoyi.asset.service.IChangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * @author FATHER
 */
public class ChangeFallbackFactory implements FallbackFactory<IChangeService> {
    private static final Logger log = LoggerFactory.getLogger(ChangeFallbackFactory.class);
    @Override
    public IChangeService create(Throwable cause) {
        log.error("资产变动审批失败{}",cause.getMessage());
        return null;
    }
}
