package com.yz.local.transaction;

import java.util.Scanner;

/**
 * 断言使用demo
 * @author yunze
 * @date 2023/11/26 0026 14:47
 */
public class AssertDemo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(1);

        System.out.print("请输入参数: ");
        boolean flag = scanner.nextBoolean();
        System.out.println("输入的参数是: " + flag);

        // 此处使用了断言，但默认情况下，JVM对assert语句是禁用的。要启用断言检查，可以使用 -enableassertions 参数启动程序。
        // 如果没有启动，那么即使flag为false，最后控制还是会打印出数字2
        assert flag : "输入的false, 不再继续往下执行";

        System.out.println(2);
    }
}
