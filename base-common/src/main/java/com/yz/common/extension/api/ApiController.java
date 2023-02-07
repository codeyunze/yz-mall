package com.yz.common.extension.api;

import com.yz.common.vo.Result;
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

    protected <T> Result<T> failed(String msg) {
        return Result.failed(msg);
    }
}
