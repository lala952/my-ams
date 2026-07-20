package com.ruoyi.asset.designPatterns.strategy.discount.context;

import com.ruoyi.asset.designPatterns.strategy.discount.service.DiscountStrategy;

public class DiscountContext {
    /**
     * 上下文客户端，持有策略对象，统一调用入口
     */
    private DiscountStrategy strategy;

    /**
     * 构造方法，传入策略对象
     */
    public DiscountContext(DiscountStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 切换策略（动态更换算法）
     */
    public void setStrategy(DiscountStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 统一调用策略方法，计算最终价格
     */
    public double getFinalPrice(double originalPrice) {
        return strategy.calculateDiscount(originalPrice);
    }
}
