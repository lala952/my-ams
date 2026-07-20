package com.ruoyi.asset.designPatterns.strategy.discount.service.impl;

import com.ruoyi.asset.designPatterns.strategy.discount.service.DiscountStrategy;

public class NewUserDiscount implements DiscountStrategy {

    /**
     * 新用户折扣，9折
     */
    @Override
    public double calculateDiscount(double originalPrice) {
        System.out.println("新用户折扣：9折");
        return originalPrice * 0.9;
    }
}
