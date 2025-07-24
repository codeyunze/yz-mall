package com.yz.mall.base.validators;

import com.yz.mall.base.validators.constraint.ValidatorConditionalRequired;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 有条件必填参数校验
 * <br>conditionFields、conditionValues、requireFields的顺序要对应
 * <br>@ConditionalRequired(paramClazz = DetectedDataApprovedDto.class
 *         , configs = {
 *         "{\"conditionField\":\"typeField\",\"conditionValue\":\"email\",\"requireField\":\"emailField\",\"message\":\"typeField字段值为email时，emailField字段值不能为空！\",\"clear\":\"false\"}"
 *         , "{\"conditionField\":\"typeField\",\"conditionValue\":\"phone,weChat\",\"requireField\":\"phoneField\",\"message\":\"typeField字段值为phone或weChat时，phoneField字段值不能为空！（如果typeField字段值不为phone和weChat时，会自动清空phoneField字段值。）\",\"clear\":\"true\"}"
 * })
 *
 * @author yunze
 * @since 2025/3/17 14:24
 */
@Documented
@Constraint(validatedBy = ValidatorConditionalRequired.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalRequired {

    /**
     * 使用参数类的类型
     */
    Class<?> paramClazz() default Object.class;

    /**
     * 校验配置规则<p>
     * conditionField   条件字段名<p>
     * conditionValue   条件字段值为多少时，必填字段为必填（如果conditionValue的字段值有多个，则使用","隔开）<p>
     * requireField     必填字段名<p>
     * message          提示信息<p>
     * clear            当requireField字段为非必填时，是否清空requireField字段值，true-清空，false-不清空<p>
     *
     * @return {"conditionField":"typeField","conditionValue":"phone","requireField":"phoneField","message":"当typeField字段值为“phone”时，phoneField字段不能为空","clear":"true"}
     */
    String[] configs() default {};

    String message() default "参数不能为空";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}