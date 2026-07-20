package com.ruoyi.asset.designPatterns.strategy.discount;

import com.ruoyi.asset.designPatterns.strategy.discount.context.DiscountContext;
import com.ruoyi.asset.designPatterns.strategy.discount.service.impl.FestivalDiscount;
import com.ruoyi.asset.designPatterns.strategy.discount.service.impl.MemberDiscount;
import com.ruoyi.asset.designPatterns.strategy.discount.service.impl.NewUserDiscount;

public class testDiscountStrategy {
    public static void main(String[] args) {
        // 商品原价
        double originalPrice = 100.0f;

        // 1.新用户购买，使用新用户策略
        DiscountContext context = new DiscountContext(new NewUserDiscount());
        System.out.println("新用户购买，最终价格为：" + "￥" + context.getFinalPrice(originalPrice));

        // 2.切换为会员策略，动态切换，无需修改上下文代码
        context.setStrategy(new MemberDiscount());
        System.out.println("会员购买，最终价格为：" + "￥" + context.getFinalPrice(originalPrice));

        // 3.切换为节日策略
        context.setStrategy(new FestivalDiscount());
        System.out.println("节日购买，最终价格为：" + "￥" + context.getFinalPrice(originalPrice));
    }
}
