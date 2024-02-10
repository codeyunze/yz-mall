package com.yz.mall.design.strategy.case1;

/**
 * 衣服-大衣
 * @author yunze
 * @date 2024/1/22 13:01
 */
public class OvercoatClothes implements Clothes {

    @Override
    public void type() {
        System.out.println("大衣");
    }

    @Override
    public void function() {
        System.out.println("御寒");
    }
}
