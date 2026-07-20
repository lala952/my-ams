package com.ruoyi.asset.designPatterns.strategy.discount.service;

public interface DiscountStrategy {
    // 计算折扣后价格（参数：原价）
    double calculateDiscount(double originalPrice);
}
