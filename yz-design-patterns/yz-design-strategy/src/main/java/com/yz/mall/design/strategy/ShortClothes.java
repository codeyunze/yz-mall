package com.yz.mall.design.strategy;

/**
 * @author yunze
 * @date 2024/1/22 12:54
 */
public class ShortClothes extends Clothes {

    @Override
    public void type() {
        System.out.println("短袖");
    }

    @Override
    public void function() {
        System.out.println("凉快");
    }
}
