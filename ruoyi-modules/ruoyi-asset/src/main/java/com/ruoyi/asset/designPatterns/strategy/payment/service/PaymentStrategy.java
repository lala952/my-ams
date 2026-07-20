package com.ruoyi.asset.designPatterns.strategy.payment.service;

import com.ruoyi.asset.designPatterns.strategy.payment.utils.PaymentResult;

/**
 * Java设计模式 - 策略模式 案例1
 *
 * 支付方式策略接口（如：支付宝、微信、信用卡）
 */
public interface PaymentStrategy {
    /**
     * 支付方法
     *
     * @param amount 支付金额
     * @return 支付结果
     */
    PaymentResult pay(double amount);

    /**
     * 获取支付方式名称
     *
     */
    String getMethodName();
}
