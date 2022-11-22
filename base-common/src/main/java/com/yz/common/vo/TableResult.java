package com.yz.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yz.common.enums.CodeEnum;

import java.util.Collections;

/**
 * @ClassName TableResult
 * @Description 后端返回给前端的列表数据对象
 * @Author yunze
 * @Date 2022/11/22 23:49
 * @Version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TableResult extends Result{

    private Integer total;

    public TableResult(int code, Object data, String message) {
        super(code, data, message);
    }

    public TableResult(int code, Integer total, Object data, String message) {
        super(code, data, message);
        this.total = total;
    }

    public static TableResult success(Integer total, Object data) {
        return new TableResult(CodeEnum.SUCCESS.get(), total, data, "成功");
    }

    public static TableResult success() {
        return new TableResult(CodeEnum.SUCCESS.get(), 0, Collections.emptyList(), "成功");
    }

    public static TableResult fail() {
        return new TableResult(CodeEnum.BUSINESS_ERROR.get(), 0, Collections.emptyList(), "失败");
    }

    public static TableResult fail(String message) {
        return new TableResult(CodeEnum.BUSINESS_ERROR.get(), 0, Collections.emptyList(), message);
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
