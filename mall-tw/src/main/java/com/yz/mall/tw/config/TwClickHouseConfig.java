package com.yz.mall.tw.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * ClickHouse 访问装配（HTTP，避免 clickhouse-jdbc SPI 干扰 MySQL dynamic-datasource）。
 * <p>
 * 开关：{@code tw.telemetry.clickhouse.enabled=true}
 */
@Configuration
@ConditionalOnProperty(prefix = "tw.telemetry.clickhouse", name = "enabled", havingValue = "true")
public class TwClickHouseConfig {

    /**
     * 供轨迹写读复用的 JDK HttpClient。
     */
    @Bean(name = "clickHouseHttpClient")
    public HttpClient clickHouseHttpClient() {
        return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }
}
