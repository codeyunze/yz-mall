package com.yz.mall.design.strategy;

/**
 * 衣服
 * @author yunze
 * @date 2024/1/22 12:49
 */
public abstract class Clothes {

    public void print() {
        System.out.println("---------衣服属性---------");
        System.out.println("衣服类型：");
        this.type();
        System.out.println("衣服功能：");
        this.function();
    }

    /**
     * 衣服类型
     */
    abstract void type();

    /**
     * 衣服功能
     */
    abstract void function();
}
