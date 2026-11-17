package com.yz.mall.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关 Sentinel 限流规则，先由本地配置加载。
 *
 * @author yunze
 * @date 2026/11/17
 */
@Data
@Component
@ConfigurationProperties(prefix = "yz.gateway.sentinel")
public class GatewaySentinelProperties {

    /**
     * 是否加载下方规则
     */
    private boolean enabled = true;

    /**
     * 接口限流规则
     */
    private List<ApiRule> rules = new ArrayList<>();

    /**
     * 按路径分组的限流规则。
     */
    @Data
    public static class ApiRule {

        /**
         * Sentinel 资源名，同一组路径共用一个计数器
         */
        private String resource;

        /**
         * 统计窗口内允许的请求数
         */
        private double count = 100;

        /**
         * 统计窗口，单位秒
         */
        private int intervalSec = 1;

        /**
         * 匹配路径，支持 {@code /sys/**} 前缀和精确路径
         */
        private List<String> paths = new ArrayList<>();
    }
}
