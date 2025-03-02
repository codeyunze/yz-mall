package com.yz.mall.file.exception;


import com.yz.mall.web.enums.CodeEnum;
import io.github.codeyunze.exception.DataNotExistException;
import io.github.codeyunze.exception.TypeNotSupportedException;
import io.github.codeyunze.utils.Result;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局异常捕获
 *
 * @author yunze
 * @since 2024/6/19 星期三 22:57
 */
@RestControllerAdvice
public class QofOverallExceptionHandle {

    /**
     * 参数校验异常提示
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    Result<?> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {
        return new Result<>(CodeEnum.PARAMS_ERROR.get(), null, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    /**
     * 数据不存在异常问题处理
     */
    @ExceptionHandler(DataNotExistException.class)
    Result<?> dataNotExistExceptionHandle(DataNotExistException e) {
        return new Result<>(CodeEnum.NOT_EXIST_ERROR.get(), null, e.getMessage());
    }


    /**
     * 不支持操作文件类型异常问题处理
     */
    @ExceptionHandler(TypeNotSupportedException.class)
    Result<?> typeNotSupportedExceptionHandle(TypeNotSupportedException e) {
        return new Result<>(CodeEnum.BUSINESS_ERROR.get(), null, e.getMessage());
    }


    /**
     * 非法参数异常问题处理
     */
    @ExceptionHandler(IllegalArgumentException.class)
    Result<?> illegalArgumentExceptionHandle(IllegalArgumentException e) {
        return new Result<>(CodeEnum.OTHER_ERROR.get(), null, e.getMessage());
    }
}
