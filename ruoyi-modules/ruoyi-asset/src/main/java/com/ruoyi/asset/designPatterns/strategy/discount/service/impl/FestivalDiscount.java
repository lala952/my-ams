package com.ruoyi.asset.designPatterns.strategy.discount.service.impl;

import com.ruoyi.asset.designPatterns.strategy.discount.service.DiscountStrategy;

public class FestivalDiscount implements DiscountStrategy {

    /**
     * 节假日折扣，7折
     */
    @Override
    public double calculateDiscount(double originalPrice) {
        System.out.println("节日折扣：7折");
        return originalPrice * 0.7;
    }
}
