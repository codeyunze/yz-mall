package com.yz.mall.tw.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * ClickHouse 数据源。
 * <p>
 * 需同时满足：classpath 含 {@code clickhouse-jdbc}，且 {@code tw.telemetry.clickhouse.enabled=true}。
 * 默认不引入 JDBC 依赖，避免驱动 SPI 干扰 MySQL dynamic-datasource 启动。
 */
@Configuration
@ConditionalOnClass(name = "com.clickhouse.jdbc.ClickHouseDriver")
@ConditionalOnProperty(prefix = "tw.telemetry.clickhouse", name = "enabled", havingValue = "true")
public class TwClickHouseConfig {

    /**
     * 轨迹库 JDBC 数据源，供后续批量写入与轨迹查询使用。
     *
     * @param properties 遥测配置
     * @return ClickHouse DataSource
     */
    @Bean(name = "clickHouseDataSource")
    public DataSource clickHouseDataSource(TwTelemetryProperties properties) {
        TwTelemetryProperties.Clickhouse ch = properties.getClickhouse();
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName("clickhouse-tw");
        dataSource.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");
        dataSource.setJdbcUrl(resolvePlaceholders(ch.getUrl()));
        dataSource.setUsername(ch.getUsername());
        dataSource.setPassword(ch.getPassword() == null ? "" : ch.getPassword());
        dataSource.setMaximumPoolSize(8);
        dataSource.setMinimumIdle(1);
        return dataSource;
    }

    /**
     * 解析 URL 中常见的 ${ENV:default} 占位（Spring 未展开到自定义字段时兜底）。
     */
    private static String resolvePlaceholders(String url) {
        if (url == null || url.isEmpty()) {
            return "jdbc:clickhouse://127.0.0.1:8123/tw";
        }
        String resolved = url;
        resolved = replaceEnv(resolved, "CLICKHOUSE_HOST", "127.0.0.1");
        resolved = replaceEnv(resolved, "CLICKHOUSE_PORT", "8123");
        return resolved;
    }

    private static String replaceEnv(String text, String envKey, String defaultValue) {
        String token = "${" + envKey + ":" + defaultValue + "}";
        if (!text.contains(token) && !text.contains("${" + envKey + "}")) {
            return text;
        }
        String value = System.getenv(envKey);
        if (value == null || value.isEmpty()) {
            value = defaultValue;
        }
        return text.replace("${" + envKey + ":" + defaultValue + "}", value).replace("${" + envKey + "}", value);
    }
}
