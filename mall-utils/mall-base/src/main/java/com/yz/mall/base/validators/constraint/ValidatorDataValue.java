package com.yz.mall.base.validators.constraint;

import com.yz.mall.base.validators.ValidDataValue;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 数据值校验
 *
 * @author yunze
 * @since 2025/3/17 11:44
 */
public class ValidatorDataValue implements ConstraintValidator<ValidDataValue, Object> {

    private Integer setValue;


    @Override
    public void initialize(ValidDataValue constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.setValue = constraintAnnotation.setValue();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return setValue.equals(value);
    }
}
