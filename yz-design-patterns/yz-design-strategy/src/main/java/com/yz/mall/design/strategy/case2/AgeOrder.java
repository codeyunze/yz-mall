package com.yz.mall.design.strategy.case2;

import java.util.Comparator;

/**
 * 根据年龄排序
 * @author yunze
 * @date 2024/1/23 18:50
 */
public class AgeOrder implements Comparator<Person> {

    @Override
    public int compare(Person o1, Person o2) {
        if (o1.getAge() > o2.getAge()) {
            return 1;
        } else if (o1.getAge() < o2.getAge()) {
            return -1;
        }
        return 0;
    }
}
