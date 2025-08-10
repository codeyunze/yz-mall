package com.yz.mall.base.validators.constraint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yz.mall.base.exception.ParamsException;
import com.yz.mall.base.validators.ConditionalRequired;
import com.yz.mall.json.JacksonUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 数据值校验
 *
 * @author yunze
 * @since 2025/3/17 11:44
 */
public class ValidatorConditionalRequired implements ConstraintValidator<ConditionalRequired, Object> {

    private Class<?> paramClazz;

    private String[] configs;

    @Override
    public void initialize(ConditionalRequired constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.paramClazz = constraintAnnotation.paramClazz();
        this.configs = constraintAnnotation.configs();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            for (String config : configs) {
                Map<String, Object> json = JacksonUtil.getObjectMapper().readValue(config, Map.class);
                String conditionField = json.get("conditionField").toString();
                String conditionValueStr = json.get("conditionValue").toString();
                List<String> conditionValue = Arrays.asList(conditionValueStr.split(","));
                String requireField = json.get("requireField").toString();
                String message = json.get("message").toString();
                boolean clear = Boolean.parseBoolean(json.get("clear").toString());

                Method conditionGetMethod = paramClazz.getMethod("get" + conditionField.substring(0, 1).toUpperCase() + conditionField.substring(1));
                Method requireFieldGetMethod = paramClazz.getMethod("get" + requireField.substring(0, 1).toUpperCase() + requireField.substring(1));

                Object conditionInvoke = conditionGetMethod.invoke(value);
                if (conditionInvoke == null) {
                    throw new ParamsException(conditionField + "值不能为空");
                }
                if (!conditionValue.contains(conditionInvoke.toString())) {
                    if (!clear) {
                        continue;
                    }
                    // 非必填，清空校验字段的值
                    if (requireFieldGetMethod.invoke(value) == null) {
                        continue;
                    }
                    Method requireFieldSetMethod = paramClazz.getMethod("set" + requireField.substring(0, 1).toUpperCase() + requireField.substring(1), String.class);
                    requireFieldSetMethod.invoke(value, (Object) null);
                    continue;
                }

                // 条件字段值匹配上必填字段必填时要求的值，校验必填字段是否有值
                Object requireInvoke = requireFieldGetMethod.invoke(value);
                if (requireInvoke == null || !StringUtils.hasText(requireInvoke.toString().trim())) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(message).addPropertyNode(requireField).addConstraintViolation();
                    return false;
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
