package com.yz.mall.design.strategy.case1;

/**
 * 鞋-凉鞋
 *
 * @author yunze
 * @date 2024/1/22 0022 19:46
 */
public class SandalShoes implements Shoes {
    @Override
    public void type() {
        System.out.println("凉鞋");
    }
}
