package com.yz.mall.design.strategy.case2;

/**
 * 根据体重排序
 * @author yunze
 * @date 2024/1/23 19:06
 */
public class WeightOrder implements Comparator<Person> {

    @Override
    public int compare(Person t1, Person t2) {
        if (t1.getWeight() > t2.getWeight()) {
            return 1;
        } else if (t1.getWeight() < t2.getWeight()) {
            return -1;
        }
        return 0;
    }
}
