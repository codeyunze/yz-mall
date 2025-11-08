package com.yz.base.list;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yunze
 * @since 2025/10/18 17:15
 */
public class ListDemo {
    public static void main(String[] args) {
        List<String> list = new LinkedList<>();

        CopyOnWriteArrayList<String> cowal = new CopyOnWriteArrayList<>();
        cowal.add("1");
        cowal.add("2");
        cowal.forEach(System.out::println);
        cowal.get(0);
    }
}
