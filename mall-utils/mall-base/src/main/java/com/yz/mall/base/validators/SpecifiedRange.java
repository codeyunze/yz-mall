package com.yz.mall.base.validators;

import com.yz.mall.base.validators.constraint.ValidatorSpecifiedRange;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 参数值必须在指定的值范围内
 * <br>如果设置为@SpecifiedRange(allowed = {"0", "1"})，则表示被注解标记的字段的值只允许为0和1
 *
 * @author yunze
 * @since 2025/3/17 16:36
 */
@Documented
@Target(ElementType.FIELD)
@Constraint(validatedBy = {ValidatorSpecifiedRange.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface SpecifiedRange {

    /**
     * 校验值
     */
    String[] allowed() default {};

    String message() default "参数不符合规范";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
