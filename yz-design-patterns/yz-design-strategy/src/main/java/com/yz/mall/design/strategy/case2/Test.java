package com.yz.mall.design.strategy.case2;

// import java.util.Arrays;

/**
 * @author yunze
 * @date 2024/1/23 10:57
 */
public class Test {
    public static void main(String[] args) {
        Person[] persons = new Person[5];
        persons[0] = new Person("张三", 21, 59.9F, 172.5F);
        persons[1] = new Person("李四", 26, 72F, 179.0F);
        persons[2] = new Person("王五", 17, 62F, 183.3F);
        persons[3] = new Person("赵六", 15, 55.5F, 169.4F);
        persons[4] = new Person("刘七", 28, 77F, 178.9F);

        System.out.println("排序前==========");
        for (Person person : persons) {
            System.out.println(person.toString());
        }

        Arrays.sort(persons, new WeightOrder());
        System.out.println("体重排序后==========");
        for (Person person : persons) {
            System.out.println(person.toString());
        }

        Arrays.sort(persons, new HeightOrder());
        System.out.println("身高排序后==========");
        for (Person person : persons) {
            System.out.println(person.toString());
        }
    }
}
