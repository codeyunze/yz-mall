package com.yz.dynamic.datasource.one.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.yz.tools.enums.DataSourceTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设置多数据源的路由
 *
 * @author yunze
 * @date 2023/10/29 0029 23:43
 */
@Primary
@Component
public class DynamicDataSource extends AbstractRoutingDataSource {

    public static ThreadLocal<String> name = new ThreadLocal<>();

    private final Map<Object, Object> dataSourceMap = new ConcurrentHashMap<>();


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
        return name.get();
    }

    @Override
    public void afterPropertiesSet() {
        // 为targetDataSources初始化所有数据源
        dataSourceMap.put(DataSourceTypeEnum.product.get(), productDataSource);
        dataSourceMap.put(DataSourceTypeEnum.stock.get(), stockDataSource);
        super.setTargetDataSources(dataSourceMap);

        // 设置默认数据源
        super.setDefaultTargetDataSource(productDataSource);
        super.afterPropertiesSet();
    }

    public void addDataSource(String key, DruidDataSource dataSource) {
        dataSourceMap.put(key, dataSource);
    }
}
