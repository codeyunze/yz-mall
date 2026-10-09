package com.yz.mall.tw.access.mqtt;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Scope;
import co.elastic.apm.api.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.tw.access.config.TwAccessProperties;
import com.yz.mall.tw.access.dto.EmqxGpsBridgeRequest;
import com.yz.mall.tw.access.service.TelemetryBridgeService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Eclipse Paho 连接 EMQX：订阅上行 GPS、下行 publish。
 * <p>
 * 链路：车机 → EMQX →（本客户端 subscribe）→ Kafka → mall-tw。
 * 对齐 <a href="https://docs.emqx.com/zh/cloud/latest/connect_to_deployments/java_sdk.html">EMQX Cloud Java SDK</a>。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmqxPahoMqttClient implements ApplicationRunner {

    private final TwAccessProperties properties;
    private final TelemetryBridgeService telemetryBridgeService;
    private volatile MqttClient client;

    @Override
    public void run(ApplicationArguments args) {
        TwAccessProperties.Mqtt mqtt = properties.getMqtt();
        if (!mqtt.isEnabled()) {
            log.info("EMQX Paho 未启用（tw.access.mqtt.enabled=false）");
            return;
        }
        try {
            connect();
        } catch (Exception ex) {
            log.warn("EMQX Paho 启动连接失败，将在首次发布/重连时重试: {}", ex.getMessage());
        }
    }

    /**
     * 建立或复用连接后发布。
     *
     * @param topic   MQTT Topic
     * @param payload 消息体
     * @param qos     QoS 0/1/2
     */
    public void publish(String topic, String payload, int qos) {
        TwAccessProperties.Mqtt mqtt = properties.getMqtt();
        if (!mqtt.isEnabled()) {
            throw new BusinessException("EMQX Paho 未启用，请配置 tw.access.mqtt.enabled=true");
        }
        try {
            ensureConnected();
            MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
            message.setQos(qos);
            message.setRetained(false);
            client.publish(topic, message);
            log.info("MQTT 下行已发布 topic={} qos={}", topic, qos);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("MQTT 发布失败: " + ex.getMessage());
        }
    }

    /**
     * 下行发布前保证已连接；断线则同步重连。
     */
    private synchronized void ensureConnected() throws Exception {
        if (client != null && client.isConnected()) {
            return;
        }
        connect();
        if (client == null || !client.isConnected()) {
            throw new BusinessException("EMQX Paho 未连接，请检查 tw.access.mqtt.*");
        }
    }

    /**
     * 建立平台侧 MQTT 长连接（账号见 {@code tw.access.mqtt.*}，勿与车机 clientId 冲突）。
     * <p>
     * 连接成功后订阅上行 GPS；自动重连场景依赖 {@link MqttCallbackExtended#connectComplete} 再订一次。
     */
    private synchronized void connect() {
        TwAccessProperties.Mqtt mqtt = properties.getMqtt();
        if (mqtt.getBroker() == null || mqtt.getBroker().isBlank()) {
            throw new BusinessException("未配置 tw.access.mqtt.broker");
        }
        try {
            // 丢弃已失效的旧客户端，避免重复占用 clientId
            if (client != null) {
                try {
                    if (client.isConnected()) {
                        return;
                    }
                    client.close();
                } catch (Exception ignored) {
                }
            }
            String clientId = mqtt.getClientId();
            if (clientId == null || clientId.isBlank()) {
                clientId = MqttClient.generateClientId();
            }
            MqttClient mqttClient = new MqttClient(mqtt.getBroker(), clientId, new MemoryPersistence());
            // cleanSession=true 时重连不会恢复订阅，须在 connectComplete 里补订
            mqttClient.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    log.info("EMQX Paho 已连接 reconnect={} uri={}", reconnect, serverURI);
                    subscribeGps(mqttClient);
                }

                @Override
                public void connectionLost(Throwable cause) {
                    log.warn("EMQX Paho 连接丢失: {}", cause == null ? "" : cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    // 消费上行 GPS 消息
                    onGpsMessage(topic, message);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    log.debug("EMQX Paho deliveryComplete");
                }
            });
            MqttConnectOptions opts = new MqttConnectOptions();
            opts.setAutomaticReconnect(mqtt.isAutomaticReconnect());
            opts.setCleanSession(mqtt.isCleanSession());
            opts.setConnectionTimeout(mqtt.getConnectionTimeout());
            opts.setKeepAliveInterval(mqtt.getKeepAliveInterval());
            if (mqtt.getUsername() != null && !mqtt.getUsername().isBlank()) {
                opts.setUserName(mqtt.getUsername());
            }
            if (mqtt.getPassword() != null && !mqtt.getPassword().isBlank()) {
                opts.setPassword(mqtt.getPassword().toCharArray());
            }
            // Serverless / ssl:// 需挂 CA；tcp:// 可跳过
            if (mqtt.getBroker().startsWith("ssl://") || mqtt.getBroker().startsWith("wss://")) {
                if (mqtt.getCaCertPath() != null && !mqtt.getCaCertPath().isBlank()) {
                    opts.setSocketFactory(MqttSslSupport.singleSocketFactory(mqtt.getCaCertPath()));
                }
            }
            log.info("Connecting to broker: {}", mqtt.getBroker());
            mqttClient.connect(opts);
            this.client = mqttClient;
            log.info("Connected to broker: {}", mqtt.getBroker());
            // 部分环境下 connectComplete 与 connect 返回时序不定，再订一次保证首连必订阅
            subscribeGps(mqttClient);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("EMQX Paho 连接失败: " + ex.getMessage());
        }
    }

    /**
     * 订阅 {@code {topicPrefix}/+/up/gps}，承接车机上行定位。
     * <p>
     * 平台账号须在 EMQX ACL 允许 subscribe 该 filter。
     *
     * @param mqttClient 已连接的 Paho 客户端
     */
    private void subscribeGps(MqttClient mqttClient) {
        TwAccessProperties.Mqtt mqtt = properties.getMqtt();
        if (!mqtt.isSubscribeGps()) {
            return;
        }
        // 例：titan/+/up/gps，与车机发布 Topic 约定一致
        String filter = properties.getTopicPrefix() + "/+/up/gps";
        try {
            mqttClient.subscribe(filter, mqtt.getSubscribeQos());
            log.info("EMQX Paho 已订阅上行 GPS filter={} qos={}", filter, mqtt.getSubscribeQos());
        } catch (Exception ex) {
            log.error("EMQX Paho 订阅失败 filter={}: {}", filter, ex.getMessage());
        }
    }

    /**
     * 上行 GPS 回调：解析 JSON 后桥接到 Kafka（与 HTTP {@code /emqx/bridge/gps} 共用逻辑）。
     * <p>
     * Paho 回调线程无 HTTP 事务，需手动 {@link ElasticApm#startTransaction()}，日志 MDC 才有 {@code trace.id}。
     *
     * @param topic   实际 Topic，如 titan/{vin}/up/gps
     * @param message MQTT 报文
     */
    private void onGpsMessage(String topic, MqttMessage message) {
        // 只处理 GPS 上行后缀，避免误收其它订阅
        if (topic == null || !topic.endsWith("/up/gps")) {
            return;
        }
        Transaction transaction = ElasticApm.startTransaction();
        transaction.setName("MQTT up/gps");
        transaction.setType("messaging");
        transaction.setLabel("mqtt.topic", topic);
        try (Scope scope = transaction.activate()) {
            String raw = new String(message.getPayload(), StandardCharsets.UTF_8);
            log.info("收到 EMQX 上行 GPS topic={} bytes={}", topic, message.getPayload() == null ? 0 : message.getPayload().length);
            EmqxGpsBridgeRequest req = new EmqxGpsBridgeRequest();
            req.setTopic(topic);
            JsonNode node = JacksonUtil.getObjectMapper().readTree(raw);
            req.setPayload(node);
            boolean sent = telemetryBridgeService.bridgeGps(req);
            if (!sent) {
                log.warn("上行 GPS 未投递 Kafka topic={}", topic);
                transaction.setResult("skip");
            } else {
                transaction.setResult("success");
            }
        } catch (Exception ex) {
            transaction.captureException(ex);
            transaction.setResult("error");
            log.error("处理上行 GPS 失败 topic={}: {}", topic, ex.getMessage(), ex);
        } finally {
            transaction.end();
        }
    }

    @PreDestroy
    public synchronized void destroy() {
        if (client == null) {
            return;
        }
        try {
            if (client.isConnected()) {
                client.disconnect();
                log.info("EMQX Paho Disconnected");
            }
            client.close();
        } catch (Exception ex) {
            log.warn("EMQX Paho 关闭异常: {}", ex.getMessage());
        } finally {
            client = null;
        }
    }
}
