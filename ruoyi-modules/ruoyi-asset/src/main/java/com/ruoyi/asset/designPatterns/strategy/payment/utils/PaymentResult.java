package com.ruoyi.asset.designPatterns.strategy.payment.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResult {

    /**
     * 支付结果封装
     */
    private boolean success;
    /**
     * 支付结果信息
     */
    private String message;
    /**
     * 实际支付金额（可能含手续费）
     */
    private double actualAmount;

}
