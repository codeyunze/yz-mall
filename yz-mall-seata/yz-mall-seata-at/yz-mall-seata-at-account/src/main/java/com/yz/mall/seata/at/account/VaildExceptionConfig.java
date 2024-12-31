package com.yz.mall.seata.at.account;

import com.yz.mall.web.common.Result;
import com.yz.mall.web.enums.CodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

/**
 * @author yunze
 * @date 2023/12/2 0002 14:52
 */
@Slf4j
@ControllerAdvice
public class VaildExceptionConfig {

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> throwCustomException(MethodArgumentNotValidException methodArgumentNotValidException) {

        log.error("[ 参数异常捕获 ] " + methodArgumentNotValidException.getMessage());
        return new Result<>(CodeEnum.PARAMS_ERROR.get(), "", Objects.requireNonNull(methodArgumentNotValidException.getBindingResult().getFieldError()).getDefaultMessage());
    }
}
