package com.yz.mall.base;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询请求
 *
 * @author yunze
 * @version 1.0
 * @date 2023/2/9 0:16
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
