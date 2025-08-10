package com.yz.mall.base.exception;

import com.yz.mall.base.enums.CodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

/**
 * 参数异常
 * @deprecated 判断是否可弃用，调整为直接使用MethodArgumentNotValidException
 * @author yunze
 */
@Deprecated
@EqualsAndHashCode(callSuper = true)
@Data
public class ParamsException extends RuntimeException {

    private int code;

    public ParamsException(String message) {
        super(StringUtils.hasText(message) ? message : CodeEnum.PARAMS_ERROR.getMsg());
        this.code = CodeEnum.PARAMS_ERROR.get();
    }
}
