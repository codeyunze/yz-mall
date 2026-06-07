# mall-kafka 案例说明

## Kafka 连接配置（重要）

### 1. 宿主机 UnknownHostException: kafka

Docker 若只配置 `KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092`，宿主机应用 bootstrap 成功后，元数据仍返回 `kafka:9092`，宿主机无法解析。

**宿主机应用请连 EXTERNAL 端口 9094：**

```yaml
spring.kafka.bootstrap-servers: 192.168.3.237:9094
```

### 2. KRaft 启动失败 controller.listener.names

KRaft 模式下 **`CONTROLLER://:9093` 不能删**，且 `KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER` 必须与 `listeners` 中的名称一致。

错误示例（会启动失败）：

```yaml
# 缺少 CONTROLLER://:9093
KAFKA_LISTENERS: PLAINTEXT://:9092,EXTERNAL://:9094
KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER   # 报 IllegalArgumentException
```

### 正确 docker-compose 配置

在你原有可启动的配置上，**仅新增 EXTERNAL 监听器**：

```yaml
kafka:
  image: registry.cn-guangzhou.aliyuncs.com/devyunze/kafka:3.9.2
  container_name: kafka-single
  hostname: kafka
  ports:
    - "9092:9092"   # 容器内 PLAINTEXT
    - "9093:9093"   # KRaft CONTROLLER（必须映射）
    - "9094:9094"   # 宿主机 EXTERNAL
  environment:
    KAFKA_NODE_ID: 1
    KAFKA_PROCESS_ROLES: broker,controller
    KAFKA_LISTENERS: PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094
    KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,EXTERNAL://192.168.3.237:9094
    KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
    KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT,EXTERNAL:PLAINTEXT
    KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
    KAFKA_CONTROLLER_QUORUM_VOTERS: 1@localhost:9093
    KAFKA_LOG_DIRS: /var/lib/kafka/data
    CLUSTER_ID: MkU3OEVBNTcwNTJENDM2Qk
    KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
    KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
    KAFKA_AUTO_CREATE_TOPICS_ENABLE: "true"

kafka-ui:
  environment:
    KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9092
```

| Listener | 端口 | 广播地址 | 谁用 |
|----------|------|----------|------|
| PLAINTEXT | 9092 | `kafka:9092` | kafka-ui、容器内服务 |
| CONTROLLER | 9093 | （KRaft 内部） | Broker 控制器，**必须保留** |
| EXTERNAL | 9094 | `192.168.3.237:9094` | 宿主机 IDE / Spring Boot |

### 重建步骤

若之前启动失败，建议清空数据目录后重建（StorageTool 可能写入了不完整元数据）：

```bash
docker compose stop kafka kafka-ui
docker rm -f kafka-single kafka-ui
rm -rf ./kafka/data/*
docker compose up -d kafka kafka-ui
```

## 快速体验

```bash
mvn -pl mall-kafka/mall-kafka-demo spring-boot:run

curl -X POST "http://localhost:10103/kafka/send?eventType=ORDER_CREATED&payload=hello-kafka"
```

完整 compose 见 `docs/docker/docker-compose.yml`。
