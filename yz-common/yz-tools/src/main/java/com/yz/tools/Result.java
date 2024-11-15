package com.yz.tools;

import com.yz.tools.enums.CodeEnum;

import java.io.Serializable;

/**
 * 后端返回给前端的数据对象
 *
 * @author yunze
 * @date 2022/11/13 23:23
 * @version 1.0
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回状态编码
     */
    private int code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    public Result() {
    }

    public Result(int code, T data, String message) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功
     *
     * @return 请求成功返回数据
     */
    public static Result<?> success() {
        return new Result<>(CodeEnum.SUCCESS.get(), null, "成功");
    }


    /**
     * 成功
     *
     * @param data 返回前端数据信息
     * @return 请求成功返回数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(CodeEnum.SUCCESS.get(), data, "成功");
    }

    /**
     * 成功
     *
     * @param data    返回前端数据信息
     * @param message 返回前端提示信息
     * @return 请求成功返回数据
     */
    public static <T> Result<T> success(T data, String message) {
        return new Result<>(CodeEnum.SUCCESS.get(), data, message);
    }

    /**
     * 失败
     *
     * @return 请求成功返回数据
     */
    public static Result<?> error() {
        return new Result<>(CodeEnum.BUSINESS_ERROR.get(), null, "失败");
    }

    /**
     * 失败
     *
     * @param message 返回前端提示信息
     * @return 请求成功返回数据
     */
    public static Result<?> error(String message) {
        return new Result<>(CodeEnum.BUSINESS_ERROR.get(), null, message);
    }

    /**
     * 失败
     *
     * @param data    返回前端数据信息
     * @param message 返回前端提示信息
     * @return 请求成功返回数据
     */
    public static <T> Result<T> error(T data, String message) {
        return new Result<T>(CodeEnum.BUSINESS_ERROR.get(), data, message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
