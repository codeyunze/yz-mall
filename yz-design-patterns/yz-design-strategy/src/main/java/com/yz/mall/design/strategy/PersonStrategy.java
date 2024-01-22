package com.yz.mall.design.strategy;

/**
 * 人物信息策略
 * @author yunze
 * @date 2024/1/22 0022 19:41
 */
public abstract class PersonStrategy {

    public PersonStrategy() {
    }

    public PersonStrategy(Clothes clothes, Shoes shoes) {
        this.clothes = clothes;
        this.shoes = shoes;
    }

    /**
     * 人物说明
     */
    abstract void display();

    /**
     * 穿的衣服
     */
    Clothes clothes;

    /**
     * 穿的鞋
     */
    Shoes shoes;

    abstract void dressing();

    abstract void putOnShoes();

    public Clothes getClothes() {
        return clothes;
    }

    public void setClothes(Clothes clothes) {
        this.clothes = clothes;
    }

    public Shoes getShoes() {
        return shoes;
    }

    public void setShoes(Shoes shoes) {
        this.shoes = shoes;
    }
}
