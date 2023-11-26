package com.yz.dynamic.datasource.one.config;

import com.yz.tools.enums.DataSourceTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * 设置多数据源的路由
 *
 * @author yunze
 * @date 2023/10/29 0029 23:43
 */
@Slf4j
@Primary
@Component
public class DynamicDataSource extends AbstractRoutingDataSource {

    public static ThreadLocal<DataSourceTypeEnum> name = new ThreadLocal<>();

    /**
     * 注入的bean对应 {@link DataSourceConfig#productDataSource()}
     */
    @Autowired
    private DataSource productDataSource;

    /**
     * 注入的bean对应 {@link DataSourceConfig#stockDataSource()}
     */
    @Autowired
    private DataSource stockDataSource;

    @Override
    protected Object determineCurrentLookupKey() {
        // 返回当前的数据源标识
        log.info("当前数据源为：{}", name.get());
        return name.get();
    }

    @Override
    public void afterPropertiesSet() {
        // 为targetDataSources初始化所有数据源
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceTypeEnum.product, productDataSource);
        targetDataSources.put(DataSourceTypeEnum.storage, stockDataSource);
        super.setTargetDataSources(targetDataSources);

        // 设置默认数据源
        super.setDefaultTargetDataSource(productDataSource);

        super.afterPropertiesSet();
    }
}
