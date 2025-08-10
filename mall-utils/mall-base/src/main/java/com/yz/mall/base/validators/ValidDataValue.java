package com.yz.mall.base.validators;

import com.yz.mall.base.validators.constraint.ValidatorDataValue;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 数据等值校验
 * <br>如果设置为@ValidDataValue(setValue = 1)，则表示被该注解标记的字段，如果字段值等于1，则通过校验，否则无法通过校验
 *
 * @author yunze
 * @since 2025/3/17 11:41
 */
@Documented
@Constraint(validatedBy = ValidatorDataValue.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDataValue {

    /**
     * 设置值
     */
    int setValue() default 0;

    String message() default "数据不符合规范";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
