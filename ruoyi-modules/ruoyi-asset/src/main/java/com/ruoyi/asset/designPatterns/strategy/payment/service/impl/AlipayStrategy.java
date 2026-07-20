package com.ruoyi.asset.designPatterns.strategy.payment.service.impl;

import com.ruoyi.asset.designPatterns.strategy.payment.service.PaymentStrategy;
import com.ruoyi.asset.designPatterns.strategy.payment.utils.PaymentResult;

public class AlipayStrategy implements PaymentStrategy {
    private String alipayAccount;
    public AlipayStrategy(String alipayAccount) {
        this.alipayAccount = alipayAccount;
    }
    @Override
    public PaymentResult pay(double amount) {
        /** 模拟支付宝支付逻辑，无手续费 */
        System.out.printf("使用支付宝账户 %s 支付 %.2f 元\n", alipayAccount, amount);
        /** 调用支付宝 SDK 等 此处简化 */
        boolean success = amount > 0;
        return new PaymentResult(success,"支付宝支付成功", amount);
    }

    @Override
    public String getMethodName() {
        return "支付宝";
    }
}
