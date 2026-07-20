package com.ruoyi.asset.designPatterns.strategy.payment.service.impl;

import com.ruoyi.asset.designPatterns.strategy.payment.service.PaymentStrategy;
import com.ruoyi.asset.designPatterns.strategy.payment.utils.PaymentResult;

public class CreditCardStrategy implements PaymentStrategy {
    /**
     * 信用卡号
     */
    private String cardNumber;
    /**
     * CVV是指信贷卡上的三位数字
     */
    private String cvv;

    public CreditCardStrategy(String cardNumber, String cvv) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
    }
    @Override
    public PaymentResult pay(double amount) {
        // 信用卡支付：固定手续费 2 元
        double fee = 2.0;
        double actualAmount = amount + fee;
        System.out.printf("使用信用卡[尾号%s]支付 %.2f 元，手续费 %.2f 元，实付 %.2f 元%n",
                cardNumber.substring(cardNumber.length() - 4), amount, fee, actualAmount);
        // 验证 CVV 等...
        boolean success = amount > 0 && cvv != null && cvv.length() == 3;
        return new PaymentResult(success, success ? "信用卡支付成功" : "信用卡支付失败(无效CVV)", actualAmount);
    }

    @Override
    public String getMethodName() {
        return "信用卡";
    }
}
