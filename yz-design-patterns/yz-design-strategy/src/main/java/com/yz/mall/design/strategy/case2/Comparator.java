package com.yz.mall.design.strategy.case2;

/**
 * 比较器
 * @author yunze
 * @date 2024/1/23 19:02
 */
public interface Comparator<T> {

    /**
     * 比较两个参数<p>
     * 参数1大于参数2，则返回1；<p>
     * 参数1小于参数2，则返回-1；<p>
     * 参数1等于参数2，则返回0；<p>
     *
     * @param t1 参数1
     * @param t2 参数2
     */
    int compare(T t1, T t2);
}
