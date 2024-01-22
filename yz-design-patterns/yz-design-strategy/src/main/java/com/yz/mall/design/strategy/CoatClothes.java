package com.yz.mall.design.strategy;

/**
 * 衣服-外套
 * @author yunze
 * @date 2024/1/22 12:58
 */
public class CoatClothes implements Clothes {

    @Override
    public void type() {
        System.out.println("外套");
    }

    @Override
    public void function() {
        System.out.println("防风");
    }
}
