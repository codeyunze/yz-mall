package com.yz.mall.design.strategy;

/**
 * @author yunze
 * @date 2024/1/22 12:58
 */
public class CoatClothes extends Clothes {

    @Override
    void type() {
        System.out.println("外套");
    }

    @Override
    void function() {
        System.out.println("防风");
    }
}
