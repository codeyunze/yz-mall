package com.yz.dynamic.datasource.one.aspect;

import com.yz.dynamic.datasource.one.annotation.DS;
import com.yz.dynamic.datasource.one.config.DynamicDataSource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 数据源选择AOP
 * @author yunze
 * @date 2023/10/29 0029 23:30
 */
@Slf4j
@Aspect
@Component
public class DynamicDataSourceAspect implements Ordered {

    @Before("within(com.yz.*) && @annotation(ds)")
    public void before(JoinPoint joinPoint, DS ds) {
        DynamicDataSource.name.set(ds.value());
        log.info("选择的数据源：{}", ds.value());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
