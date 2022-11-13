package com.yz.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yz.common.enums.CodeEnum;

import java.io.Serializable;

/**
 * @ClassName Result
 * @Description 后端返回给前端的数据对象
 * @Author yunze
 * @Date 2022/11/13 23:23
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result implements Serializable {

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
    private Object data;

    public Result(int code, Object data, String message) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功
     *
     * @return Result
     */
    public static Result success() {
        return new Result(CodeEnum.SUCCESS.get(), null, "成功");
    }


    /**
     * 成功
     *
     * @param data 返回前端数据信息
     * @return Result
     */
    public static Result success(Object data) {
        return new Result(CodeEnum.SUCCESS.get(), data, "成功");
    }

    /**
     * 成功
     *
     * @param data    返回前端数据信息
     * @param message 返回前端提示信息
     * @return Result
     */
    public static Result success(Object data, String message) {
        return new Result(CodeEnum.SUCCESS.get(), data, message);
    }

    /**
     * 失败
     *
     * @return Result
     */
    public static Result fail() {
        return new Result(CodeEnum.BUSINESS_ERROR.get(), null, "失败");
    }

    /**
     * 失败
     *
     * @param message 返回前端提示信息
     * @return Result
     */
    public static Result fail(String message) {
        return new Result(CodeEnum.BUSINESS_ERROR.get(), null, message);
    }

    /**
     * 失败
     *
     * @param data    返回前端数据信息
     * @param message 返回前端提示信息
     * @return Result
     */
    public static Result fail(Object data, String message) {
        return new Result(CodeEnum.BUSINESS_ERROR.get(), data, message);
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
