package com.yz.mall.design.strategy.case1;

/**
 * 鞋-皮鞋
 *
 * @author yunze
 * @date 2024/1/22 0022 19:48
 */
public class LeatherShoes implements Shoes {
    @Override
    public void type() {
        System.out.println("皮鞋");
    }
}
