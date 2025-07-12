package com.yz.mall.base;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 分页查询请求
 * @author yunze
 * @date 2023/2/9 0:16
 * @version 1.0
 */
@Data
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
    @Valid
    @NotNull(message = "过滤条件不能为空")
    private T filter;
}
