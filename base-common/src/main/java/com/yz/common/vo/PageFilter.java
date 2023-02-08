package com.yz.common.vo;

import java.io.Serializable;

/**
 * @ClassName PageFilter
 * @Description 分页查询请求
 * @Author yunze
 * @Date 2023/2/9 0:16
 * @Version 1.0
 */
public class PageFilter<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 每页显示条数，默认 10
     */
    private long size = 10;

    /**
     * 当前页，默认 1
     */
    private long current = 1;

    /**
     * 过滤条件
     */
    private T filter;

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public T getFilter() {
        return filter;
    }

    public void setFilter(T filter) {
        this.filter = filter;
    }
}
