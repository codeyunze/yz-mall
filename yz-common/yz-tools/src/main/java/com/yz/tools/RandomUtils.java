package com.yz.tools;

import java.util.Random;

/**
 * 随机信息生成工具
 *
 * @author yunze
 * @date 2024/12/17 16:37
 */
public class RandomUtils {

    /**
     * 随机生成8位密码
     */
    public static String randomPassword() {
        char[] integers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        char[] lowercases = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        char[] capitals = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        char[] specials = {'!', '@', '#', '$', '%', '^', '*', '(', ')', '_', '=', '+', '~'};

        char[][] record = new char[4][2];

        for (int i = 0; i < 8; i++) {
            int oneDimensional = i % 4;
            int twoDimensional = i / 4;

            if (0 == oneDimensional) {
                int index = (int) (Math.random() * (9 + 1));
                record[oneDimensional][twoDimensional] = integers[index];
            } else if (oneDimensional == 1) {
                int index = (int) (Math.random() * (23 + 1));
                record[oneDimensional][twoDimensional] = lowercases[index];
            } else if (oneDimensional == 2) {
                int index = (int) (Math.random() * (23 + 1));
                record[oneDimensional][twoDimensional] = capitals[index];
            } else {
                int index = (int) (Math.random() * (14 + 1));
                record[oneDimensional][twoDimensional] = specials[index];
            }
        }

        int[] arr = new int[8];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        // 打乱顺序
        shuffle(arr);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(record[arr[i] % 4][arr[i] / 4]);
        }

        return sb.toString();
    }


    // 打乱数组
    private static void shuffle(int[] arr) {
        Random mRandom = new Random();
        for (int i = arr.length; i > 0; i--) {
            int rand = mRandom.nextInt(i);
            swap(arr, rand, i - 1);
        }
    }

    // 交换两个值
    private static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

}
