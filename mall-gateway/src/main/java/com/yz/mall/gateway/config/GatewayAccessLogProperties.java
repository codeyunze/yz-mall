package com.yz.mall.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关访问日志配置。
 *
 * @author yunze
 * @date 2026/9/21
 */
@Data
@Component
@ConfigurationProperties(prefix = "yz.gateway.access-log")
public class GatewayAccessLogProperties {

    /**
     * 是否打印访问日志，默认开启
     */
    private boolean enabled = true;

    /**
     * 是否脱敏 token、password 等敏感字段，默认开启
     */
    private boolean maskSensitive = true;

    /**
     * 日志中 Body 最大字符数，超出截断
     */
    private int maxBodyLogLength = 8192;

    /**
     * 不打印访问日志的路径（Ant 模式）
     */
    private List<String> skipPaths = new ArrayList<>(List.of("/actuator/**"));

    /**
     * 访问日志 Kafka 投递
     */
    private Kafka kafka = new Kafka();

    /**
     * 访问日志 Kafka 投递开关与 Topic。
     */
    @Data
    public static class Kafka {
        /**
         * 是否将访问日志写入 Kafka，默认开启
         */
        private boolean enabled = true;

        /**
         * 访问日志 Topic
         */
        private String topic = "log-gateway-access";

        /**
         * Topic 分区数（单节点 Kafka 用 1 即可）
         */
        private int partitions = 1;

        /**
         * Topic 副本数，必须 ≤ Broker 数；Docker 单节点为 1
         */
        private int replicas = 1;
    }
}
