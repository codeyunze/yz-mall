package com.yz.mall.design.strategy;

/**
 * 人物信息-男人
 * @author yunze
 * @date 2024/1/22 0022 19:52
 */
public class MenPersonStrategy extends PersonStrategy {

    public MenPersonStrategy() {
        super(new CoatClothes(), new LeatherShoes());
    }

    public MenPersonStrategy(Clothes clothes, Shoes shoes) {
        super(clothes, shoes);
    }

    @Override
    void display() {
        System.out.println("我是男性");
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
