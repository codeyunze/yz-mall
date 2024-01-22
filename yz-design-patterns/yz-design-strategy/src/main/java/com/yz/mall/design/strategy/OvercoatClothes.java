package com.yz.mall.design.strategy;

/**
 * @author yunze
 * @date 2024/1/22 13:01
 */
public class OvercoatClothes extends Clothes{

    @Override
    void type() {
        System.out.println("大衣");
    }

    @Override
    void function() {
        System.out.println("御寒");
    }
}
