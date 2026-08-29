package com.yz.mall.base.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.UnknownHostException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 全局异常捕获
 *
 * @author yunze
 * @date 2024/6/19 星期三 22:57
 */
@Order(0)
@RestControllerAdvice
public class OverallExceptionHandle {

    private static final Logger log = LoggerFactory.getLogger(OverallExceptionHandle.class);

    @ExceptionHandler(OtherException.class)
    Result<?> otherExceptionHandle(OtherException e) {
        log.error(e.getMessage(), e);
        return new Result<>(e.getCode(), null, e.getMessage());
    }

    /**
     * 业务异常问题处理
     */
    @ExceptionHandler(BusinessException.class)
    Result<?> businessExceptionHandle(BusinessException e) {
        return Result.error(e.getMessage());
    }

    /**
     * 业务数据不存在异常问题处理
     */
    @ExceptionHandler(DataNotExistException.class)
    Result<?> dataNotExistExceptionHandle(DataNotExistException e) {
        return Result.error(e.getMessage());
    }


    /**
     * 业务数据已经存在异常问题处理
     */
    @ExceptionHandler(DuplicateException.class)
    Result<?> duplicateExceptionHandle(DuplicateException e) {
        return new Result<>(CodeEnum.ALREADY_EXISTS_ERROR.get(), null, StringUtils.hasText(e.getMessage()) ? e.getMessage() : CodeEnum.ALREADY_EXISTS_ERROR.getMsg());
    }

    /**
     * 重复提交问题处理
     */
    @ExceptionHandler(RepeatSubmitException.class)
    Result<?> businessExceptionHandle(RepeatSubmitException e) {
        return new Result<>(CodeEnum.REPEAT_SUBMIT.get(), null, StringUtils.hasText(e.getMessage()) ? e.getMessage() : CodeEnum.REPEAT_SUBMIT.getMsg());
    }

    /**
     * SQL完整性约束异常
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    Result<?> sqlIntegrityConstraintViolationExceptionHandle(SQLIntegrityConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return new Result<>(CodeEnum.ALREADY_EXISTS_ERROR.get(), null, CodeEnum.ALREADY_EXISTS_ERROR.getMsg());
    }

    /**
     * Feign请求异常
     */
    @ExceptionHandler(FeignException.class)
    Result<?> feignExceptionHandle(FeignException e) {
        log.error(e.getMessage(), e);
        return new Result<>(e.getCode(), null, e.getMessage());
    }

    /**
     * 无访问业务权限问题处理
     */
    @ExceptionHandler(AuthenticationException.class)
    Result<?> authenticationExceptionHandle(AuthenticationException e) {
        return new Result<>(CodeEnum.AUTHENTICATION_ERROR.get(), null, e.getMessage());
    }

    /**
     * 服务掉线问题处理
     */
    @ExceptionHandler(UnknownHostException.class)
    Result<?> authenticationExceptionHandle(UnknownHostException e) {
        log.error(e.getMessage(), e);
        return new Result<>(CodeEnum.SYSTEM_ERROR.get(), null, "服务异常，请稍后再试");
    }

    /**
     * 参数校验异常提示
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    Result<?> methodArgumentNotValidExceptionHandle(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return new Result<>(CodeEnum.PARAMS_ERROR.get(), null, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
    }

    /**
     * 请求体解析失败（JSON 语法错误、类型不匹配、请求体缺失等）
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    Result<?> httpMessageNotReadableExceptionHandle(HttpMessageNotReadableException e) {
        String message = resolveHttpMessageNotReadableMessage(e);
        log.warn("请求体解析失败: {}", e.getMessage());
        return new Result<>(CodeEnum.PARAMS_ERROR.get(), null, message);
    }

    /**
     * 参数错误问题处理
     */
    @ExceptionHandler(value = ParamsException.class)
    public Result<?> paramsExceptionHandler(ParamsException e) {
        log.error(e.getMessage(), e);
        return new Result<>(e.getCode(), null, e.getMessage());
    }

    /**
     * 将请求体解析异常转换为对调用方友好的中文提示。
     *
     * @param e 请求体不可读异常
     * @return 友好提示文案
     */
    private String resolveHttpMessageNotReadableMessage(HttpMessageNotReadableException e) {
        String rawMessage = e.getMessage();
        if (rawMessage != null && rawMessage.contains("Required request body is missing")) {
            return "请求体不能为空";
        }
        Throwable cause = e.getMostSpecificCause();
        if (cause instanceof JsonParseException jsonParseException) {
            return resolveJsonParseMessage(jsonParseException);
        }
        if (cause instanceof InvalidFormatException invalidFormatException) {
            String field = joinJsonPath(invalidFormatException);
            String targetType = invalidFormatException.getTargetType() == null ? "正确类型" : invalidFormatException.getTargetType().getSimpleName();
            Object value = invalidFormatException.getValue();
            if (StringUtils.hasText(field)) {
                return "参数格式错误：字段 " + field + " 的值 [" + value + "] 无法转换为 " + targetType;
            }
            return "参数格式错误：值 [" + value + "] 无法转换为 " + targetType;
        }
        if (cause instanceof MismatchedInputException mismatchedInputException) {
            String field = joinJsonPath(mismatchedInputException);
            if (StringUtils.hasText(field)) {
                return "参数格式错误：字段 " + field + " 类型不匹配或结构不正确";
            }
            return "参数格式错误：请求体类型不匹配或结构不正确";
        }
        return "请求体 JSON 格式错误，请检查是否缺少逗号、引号或括号";
    }

    /**
     * 解析 JSON 语法错误位置，给出可读提示。
     *
     * @param e Jackson JSON 解析异常
     * @return 友好提示文案
     */
    private String resolveJsonParseMessage(JsonParseException e) {
        String detail = e.getOriginalMessage();
        String locationHint = "";
        if (e.getLocation() != null && e.getLocation().getLineNr() > 0) {
            locationHint = "（约第 " + e.getLocation().getLineNr() + " 行，第 " + e.getLocation().getColumnNr() + " 列）";
        }
        if (detail != null) {
            if (detail.contains("was expecting comma")) {
                return "请求体 JSON 格式错误：对象属性之间缺少逗号" + locationHint;
            }
            if (detail.contains("Unexpected character") || detail.contains("Unexpected end-of-input")) {
                return "请求体 JSON 格式错误：存在非法字符或括号/引号不匹配" + locationHint;
            }
            if (detail.contains("was expecting double-quote")) {
                return "请求体 JSON 格式错误：属性名或字符串缺少引号" + locationHint;
            }
        }
        return "请求体 JSON 格式错误，请检查是否缺少逗号、引号或括号" + locationHint;
    }

    /**
     * 拼接 Jackson 字段路径，便于定位出错字段。
     *
     * @param e JSON 映射异常
     * @return 字段路径，如 type 或 address.city
     */
    private String joinJsonPath(JsonMappingException e) {
        if (e.getPath() == null || e.getPath().isEmpty()) {
            return "";
        }
        return e.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining("."));
    }
}
