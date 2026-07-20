package com.ruoyi.asset.designPatterns.strategy.payment.client;

import com.ruoyi.asset.designPatterns.strategy.payment.service.PaymentStrategy;
import com.ruoyi.asset.designPatterns.strategy.payment.context.Order;
import com.ruoyi.asset.designPatterns.strategy.payment.service.impl.AlipayStrategy;
import com.ruoyi.asset.designPatterns.strategy.payment.service.impl.CreditCardStrategy;
import com.ruoyi.asset.designPatterns.strategy.payment.service.impl.WechatPayStrategy;
import com.ruoyi.asset.designPatterns.strategy.payment.utils.PaymentResult;

public class client {
    public static void main(String[] args) {
        /** 创建订单对象 */
        Order order = new Order("ORD-10086", 299.99);

        // 用户选择支付宝支付
        System.out.println("用户选择支付宝支付：");
        PaymentStrategy alipay = new AlipayStrategy("user@alipay.com");
        order.setPaymentStrategy(alipay);
        order.checkout();
        System.out.println("------------------------------------------------------");
        // 同一个订单，换一种支付方式（演示动态切换）
        System.out.println("用户选择微信支付：");
        PaymentStrategy wechat = new WechatPayStrategy("wx-openid-123456");
        order.setPaymentStrategy(wechat);
        order.checkout();

        System.out.println("------------------------------------------------------");

        // 使用信用卡支付
        System.out.println("用户选择信用卡支付：");
        PaymentStrategy creditCard = new CreditCardStrategy("1234 5678 9012 3456", "123");
        order.setPaymentStrategy(creditCard);
        order.checkout();
        System.out.println("----Lambda 表达式--------------------------------------");

    }
}
