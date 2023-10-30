package com.yz.dynamic.datasource.one.annotation;

import com.yz.tools.enums.DataSourceTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据源设置注解
 * @author yunze
 * @date 2023/10/29 0029 23:25
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DS {

    /**
     * 设置数据源
     * 默认：product
     */
    String value() default "product";
}
