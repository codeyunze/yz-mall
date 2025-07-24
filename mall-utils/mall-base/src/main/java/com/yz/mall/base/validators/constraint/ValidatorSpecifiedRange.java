package com.yz.mall.base.validators.constraint;

import com.yz.mall.base.validators.SpecifiedRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author yunze
 * @since 2025/3/17 16:37
 */
public class ValidatorSpecifiedRange implements ConstraintValidator<SpecifiedRange, Object> {

    private String[] allowed;

    @Override
    public void initialize(SpecifiedRange constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.allowed = constraintAnnotation.allowed();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (String str : allowed) {
            if (str.equals(value.toString().trim())) {
                return true;
            }
        }
        return false;
    }
}
