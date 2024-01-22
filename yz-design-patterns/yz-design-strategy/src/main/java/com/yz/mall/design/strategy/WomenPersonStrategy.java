package com.yz.mall.design.strategy;

/**
 * 人物信息-女人
 * @author yunze
 * @date 2024/1/22 0022 19:52
 */
public class WomenPersonStrategy extends PersonStrategy {

    public WomenPersonStrategy() {
        super(new ShortClothes(), new SandalShoes());
    }

    public WomenPersonStrategy(Clothes clothes, Shoes shoes) {
        super(clothes, shoes);
    }

    @Override
    void display() {
        System.out.println("我是女性");
    }

    @Override
    void dressing() {
        clothes.type();
        clothes.function();
    }

    @Override
    void putOnShoes() {
        shoes.type();
    }


}
