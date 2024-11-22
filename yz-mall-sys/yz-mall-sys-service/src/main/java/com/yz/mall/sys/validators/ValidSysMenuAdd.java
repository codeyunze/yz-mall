package com.yz.mall.sys.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 新增菜单信息校验注解
 *
 * @author yunze
 * @date 2024/11/22 16:17
 */
@Documented
@Constraint(validatedBy = SysMenuAddValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSysMenuAdd {
    String message() default "菜单信息校验不通过";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
