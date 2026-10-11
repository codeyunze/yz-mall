package com.yz.mall.tw.access.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 接入服务配置
 */
@Data
@ConfigurationProperties(prefix = "tw.access")
public class TwAccessProperties {

    /**
     * MQTT Topic 命名空间前缀，最终形如 titan/{vin}/up/gps
     */
    private String topicPrefix = "titan";

    /**
     * 依赖 mall-tw（OpenFeign；空 url 走 Nacos 服务名 mall-tw）
     */
    private MallTw mallTw = new MallTw();

    /**
     * 鉴权策略
     */
    private Auth auth = new Auth();

    /**
     * Kafka 桥接
     */
    private KafkaBridge kafka = new KafkaBridge();

    /**
     * EMQX MQTT（Eclipse Paho：订阅上行 GPS + 下行 publish）
     */
    private Mqtt mqtt = new Mqtt();

    /**
     * 在线状态 Redis
     */
    private Online online = new Online();

    @Data
    public static class MallTw {
        /**
         * 可选直连地址；为空则按服务名 {@code mall-tw} 负载均衡。例：http://127.0.0.1:5005
         */
        private String url = "";
    }

    @Data
    public static class Auth {
        /**
         * 开发旁路：不调 mall-tw，username/password 任意非空即放行（仅本地联调）
         */
        private boolean devBypass = false;
        /**
         * 旁路时默认绑定 VIN（可被 clientid 覆盖：若 clientid 形如 VIN 则用 clientid）
         */
        private String devDefaultVin = "TESTVIN001";
    }

    @Data
    public static class KafkaBridge {
        /**
         * 是否向 Kafka 发送遥测
         */
        private boolean enabled = true;
        /**
         * 原始遥测 Topic（与 mall-tw 消费一致）
         */
        private String telemetryTopic = "tw-telemetry-raw";
    }

    @Data
    public static class Mqtt {
        /**
         * 是否启用 Paho 连接（下行发布依赖此开关）
         */
        private boolean enabled = false;
        /**
         * Broker，如 tcp://xxx.emqx.cloud:1883 或 ssl://xxx.emqx.cloud:8883（Serverless 仅 TLS）
         */
        private String broker = "";
        /**
         * 平台侧 clientId（勿与车机冲突）
         */
        private String clientId = "mall-tw-access";
        /**
         * EMQX 认证用户名（平台账号，非车机）
         */
        private String username = "";
        /**
         * EMQX 认证密码
         */
        private String password = "";
        /**
         * 单向 TLS 时 CA 路径：文件系统路径或 {@code classpath:emqxsl-ca.crt}
         */
        private String caCertPath = "classpath:emqxsl-ca.crt";
        /**
         * 断线自动重连
         */
        private boolean automaticReconnect = true;
        /**
         * Clean Session
         */
        private boolean cleanSession = true;
        /**
         * 连接超时秒
         */
        private int connectionTimeout = 10;
        /**
         * KeepAlive 秒
         */
        private int keepAliveInterval = 60;
        /**
         * 是否订阅 {@code {topicPrefix}/+/up/gps}（车机上行）
         */
        private boolean subscribeGps = true;
        /**
         * 上行订阅 QoS
         */
        private int subscribeQos = 1;
    }

    @Data
    public static class Online {
        /**
         * Redis key 前缀，与 mall-tw 只读约定一致
         */
        private String redisPrefix = "tw:online:";
        /**
         * 在线标记值
         */
        private String onlineValue = "1";
        /**
         * TTL 秒；0 表示不过期（依赖上下线 Webhook）
         */
        private long ttlSeconds = 0L;
    }
}
