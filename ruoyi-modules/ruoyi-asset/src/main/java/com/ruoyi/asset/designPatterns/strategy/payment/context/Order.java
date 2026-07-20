package com.ruoyi.asset.designPatterns.strategy.payment.context;

import com.ruoyi.asset.designPatterns.strategy.payment.service.PaymentStrategy;
import com.ruoyi.asset.designPatterns.strategy.payment.utils.PaymentResult;

/**
 * 订单类 —— 上下文角色
 * 它不需要知道具体支付策略的细节，只需要调用策略接口即可
 */
public class Order {
    private String orderId;
    private double totalAmount;
    private PaymentStrategy paymentStrategy;  // 持有一个策略对象
    public Order(String orderId, double totalAmount) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
    }

    /**
     * 设置支付策略（可以在运行时动态改变）
     */
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }

    /**
     * 执行支付
     */
    public void checkout() {
        if (paymentStrategy == null) {
            System.out.println("请先选择支付方式");
            return;
        }

        System.out.println("订单号：" + orderId + "，总金额：" + totalAmount);
        PaymentResult result = paymentStrategy.pay(totalAmount);
        System.out.println("支付结果：" + result);

        if (result.isSuccess()) {
            System.out.println("支付成功，订单完成！");
        } else {
            System.out.println("支付失败，请重试");
        }
    }
}
