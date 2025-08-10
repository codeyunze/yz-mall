package com.yz.mall.base;


import com.yz.mall.base.enums.CodeEnum;

import java.util.List;

public class ApiController {

    public ApiController() {
    }


    protected Result error(CodeEnum code) {
        return new Result<>(code.get(), null, code.getMsg());
    }

    protected Result failed(String msg) {
        return Result.error(msg);
    }

    protected <T> Result<T> success(T data) {
        return Result.success(data);
    }

    /**
     * 列表查询成功返回信息
     *
     * @param data  列表数据
     * @param total 总数
     */
    protected <T> Result<ResultTable<T>> success(List<T> data, Long total) {
        return new Result<>(CodeEnum.SUCCESS.get(), new ResultTable<>(data, total), "查询成功");
    }
}
