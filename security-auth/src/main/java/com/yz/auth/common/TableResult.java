package com.yz.auth.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;

/**
 * @ClassName TableResult
 * @Description 后端返回给前端的列表数据对象
 * @Author yunze
 * @Date 2022/11/22 23:49
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableResult<T> extends Result<T> {

    private static final long serialVersionUID = 1L;

    /**
     * 数据总数
     */
    private Long total;

    /**
     * 数据总页数
     */
    private Long pages;

    /**
     * 列表数据查询响应信息
     *
     * @param code    响应编码
     * @param data    列表数据
     * @param total   总数
     * @param pages   页面总数
     * @param message 说明消息
     */
    public TableResult(int code, T data, Long total, Long pages, String message) {
        super(code, data, message);
        this.total = total;
        this.pages = pages;
    }

    /**
     * 响应成功
     *
     * @param data  列表数据
     * @param total 总数
     * @param pages 页面总数
     * @param <T>   返回列表数据类型
     * @return 返回查询列表数据
     */
    public static <T> TableResult<T> success(T data, Long total, Long pages) {
        return new TableResult<T>(CodeEnum.SUCCESS.get(), data, total, pages, "成功");
    }

    /**
     * 响应成功
     *
     * @param data 列表数据
     * @param <T>  返回列表数据类型
     * @return 返回查询列表数据
     */
    public static <T> TableResult<T> success(T data) {
        return TableResult.success(data, null, null);
    }

    /**
     * 响应成功
     *
     * @return 返回成功编码与成功消息
     */
    public static TableResult success() {
        return TableResult.success(Collections.emptyList());
    }

    /**
     * 返回查询失败信息
     *
     * @return 返回查询失败编码与失败消息
     */
    public static TableResult failed() {
        return TableResult.failed("失败");
    }

    /**
     * 返回查询失败信息，及失败原因
     *
     * @param message 失败原因
     * @return 返回查询失败编码与失败原因
     */
    public static TableResult failed(String message) {
        return new TableResult(CodeEnum.BUSINESS_ERROR.get(), Collections.emptyList(), null, null, message);
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }
}
