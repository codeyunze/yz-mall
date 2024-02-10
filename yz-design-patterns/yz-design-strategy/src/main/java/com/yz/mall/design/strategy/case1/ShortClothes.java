package com.yz.mall.design.strategy.case1;

/**
 * 衣服-短袖
 *
 * @author yunze
 * @date 2024/1/22 12:54
 */
public class ShortClothes implements Clothes {

    @Override
    public void type() {
        System.out.println("短袖");
    }

    @Override
    public void function() {
        System.out.println("凉快");
    }
}
