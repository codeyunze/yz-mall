package com.yz.mall.design.strategy.case1;

/**
 * @author yunze
 * @date 2024/1/22 0022 19:57
 */
public class Test {
    public static void main(String[] args) {
        PersonStrategy person = new WomenPersonStrategy();
        person.display();
        person.dressing();
        person.putOnShoes();

        System.out.println("------------------------------");

        person.setClothes(new OvercoatClothes());
        person.dressing();
        person.setShoes(new LeatherShoes());
        person.putOnShoes();
    }
}
