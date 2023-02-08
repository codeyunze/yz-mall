package com.yz.common.extension.api;

import com.yz.common.vo.Result;
import com.yz.common.vo.TableResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName ApiController
 * @Description TODO
 * @Author yunze
 * @Date 2023/2/6 23:03
 * @Version 1.0
 */
public class ApiController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

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
     * @param pages 页面总数
     * @param <T>   列表对象类型
     * @return 列表数据、数据总数、页面总数
     */
    protected <T> TableResult<T> success(T data, Long total, Long pages) {
        return TableResult.success(data, total, pages);
    }

    protected <T> Result<T> failed(String msg) {
        return Result.failed(msg);
    }
}
