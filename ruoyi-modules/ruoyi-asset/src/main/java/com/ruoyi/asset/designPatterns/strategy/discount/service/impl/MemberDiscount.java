package com.ruoyi.asset.designPatterns.strategy.discount.service.impl;

import com.ruoyi.asset.designPatterns.strategy.discount.service.DiscountStrategy;

public class MemberDiscount implements DiscountStrategy {

    /**
     * 会员折扣算法，8折
     */
    @Override
    public double calculateDiscount(double originalPrice) {
        System.out.println("会员折扣：8折");
        return originalPrice * 0.8;
    }
}
