package com.yz.mall.design.strategy.case2;

/**
 * @author yunze
 * @date 2024/1/23 18:56
 */
public class Arrays {

    public static <T> void sort(T[] a, Comparator<? super T> c) {
        int length = a.length;
        if (length < 2) {
            throw new RuntimeException("无需排序");
        }

        Integer count = 0;
        boolean swapped;
        for (int i = 0; i < length; i++) {
            swapped = false;
            for (int j = 0; j < length; j++) {
                count++;
                System.out.println("比较：" + a[i] + " 和 " + a[j]);
                if (c.compare(a[i], a[j]) < 0) {
                    T temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }

        System.out.println("比较次数：" + count);
    }
}
