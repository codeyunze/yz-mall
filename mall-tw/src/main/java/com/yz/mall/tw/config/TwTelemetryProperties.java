package com.yz.mall.tw.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 遥测与轨迹配置（节点 0 起生效；Kafka/ClickHouse 默认关闭，避免无中间件时无法启动）。
 */
@Data
@ConfigurationProperties(prefix = "tw.telemetry")
public class TwTelemetryProperties {

    /**
     * Kafka 消费开关与 Topic（节点 4 启用）
     */
    private Kafka kafka = new Kafka();

    /**
     * ClickHouse 连接（节点 3 启用轨迹写读）
     */
    private Clickhouse clickhouse = new Clickhouse();

    /**
     * 轨迹查询与批量刷盘参数
     */
    private Track track = new Track();

    @Data
    public static class Kafka {
        /**
         * 是否启用遥测 Kafka 消费，默认 false
         */
        private boolean enabled = false;
        /**
         * 原始 GPS Topic
         */
        private String topic = "tw-telemetry-raw";
        /**
         * 消费组
         */
        private String groupId = "tw-telemetry-gps";
        /**
         * 是否启用速度跳变粗过滤
         */
        private boolean speedJumpFilter = true;
        /**
         * 速度跳变阈值（km/h），超过则丢弃该点
         */
        private double maxSpeedKmh = 250D;
    }

    @Data
    public static class Clickhouse {
        /**
         * 是否装配 ClickHouse DataSource，默认 false
         */
        private boolean enabled = false;
        /**
         * JDBC URL，密码用环境变量注入
         */
        private String url = "jdbc:clickhouse://${CLICKHOUSE_HOST:127.0.0.1}:${CLICKHOUSE_PORT:8123}/tw";
        /**
         * 用户名
         */
        private String username = "default";
        /**
         * 密码（勿提交明文，用 CLICKHOUSE_PASSWORD）
         */
        private String password = "";
    }

    @Data
    public static class Track {
        /**
         * 轨迹查询最大时间窗（小时）
         */
        private int maxQueryHours = 24;
        /**
         * 默认返回最大点数
         */
        private int defaultMaxPoints = 2000;
        /**
         * 绝对上限点数
         */
        private int absoluteMaxPoints = 5000;
        /**
         * ClickHouse 批量写入条数阈值
         */
        private int batchSize = 1000;
        /**
         * 批量刷盘间隔毫秒
         */
        private long flushIntervalMs = 1000L;
    }
}
