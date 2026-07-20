package com.ruoyi.asset.designPatterns.strategy.payment.service.impl;

import com.ruoyi.asset.designPatterns.strategy.payment.service.PaymentStrategy;
import com.ruoyi.asset.designPatterns.strategy.payment.utils.PaymentResult;

public class WechatPayStrategy implements PaymentStrategy {
    private String openId;

    public WechatPayStrategy(String openId) {
        this.openId = openId;
    }
    @Override
    public PaymentResult pay(double amount) {
        // 微信支付可能有小额手续费（比如 0.1%）
        double fee = amount * 0.001;
        double actualAmount = amount + fee;
        System.out.printf("使用微信(openId=%s)支付 %.2f 元，手续费 %.2f 元，实付 %.2f 元%n",
                openId, amount, fee, actualAmount);
        boolean success = amount > 0;
        return new PaymentResult(success, "微信支付成功", actualAmount);
    }

    @Override
    public String getMethodName() {
        return "微信支付";
    }
}