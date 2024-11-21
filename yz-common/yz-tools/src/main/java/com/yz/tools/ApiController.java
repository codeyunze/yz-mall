package com.yz.tools;

import com.yz.tools.enums.CodeEnum;

import java.util.List;

public class ApiController {

    public ApiController() {
    }

    protected <T> Result<T> success(T data) {
        return Result.success(data);
    }

    /**
     * 列表查询成功返回信息
     *
     * @param data  列表数据
     * @param total 总数
     * @param <T>   列表对象类型
     * @return 列表数据、数据总数、页面总数
     */
    protected <T> TableResult<T> success(T data, Long total) {
        return TableResult.success(data, total);
    }

    protected Result failed(String msg) {
        return Result.error(msg);
    }

    /**
     * 列表查询成功返回信息
     * @param data 列表数据
     * @param total 总数
     * @return
     * @param <T>
     */
    protected <T> Result<ResultTable<T>> success(List<T> data, Long total) {
        return new Result<>(CodeEnum.SUCCESS.get(), new ResultTable<>(data, total), "查询成功");
    }
}
