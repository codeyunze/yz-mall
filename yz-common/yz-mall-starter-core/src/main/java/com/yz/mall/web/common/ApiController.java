package com.yz.mall.web.common;

import com.yz.mall.web.enums.CodeEnum;

import java.util.List;

public class ApiController {

    public ApiController() {
    }


    protected Result error(CodeEnum codeEnum) {
        return new Result<>(codeEnum.get(), null, codeEnum.getMsg());
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
