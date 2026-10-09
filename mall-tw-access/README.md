# mall-tw-access

Titan Watch **接入服务**：EMQX 鉴权 / ACL / 上下线、GPS 桥接 Kafka、下行发布代理。

| 项 | 说明 |
|---|---|
| 服务名 | `mall-tw-access` |
| 端口 | `5006` |
| 启动类 | `com.yz.mall.tw.access.TwAccessApplication` |

## 职责

```text
车机 MQTT ──► EMQX Cloud
                │
                ├─ HTTP Auth/ACL ──► POST /emqx/auth|acl（可选）
                ├─ Webhook 上下线 ──► POST /emqx/webhook ──► Redis tw:online:{vin}
                └─ Paho 订阅 titan/+/up/gps ──► mall-tw-access ──► Kafka tw-telemetry-raw
                                                                    └──► mall-tw → Redis/MySQL/CH
```

（兼容保留：`POST /emqx/bridge/gps` 供 Rule HTTP 转发，与 Paho 订阅二选一或并存均可。）
不负责：车辆/终端主数据（mall-tw）、轨迹查询（mall-tw）、控车编排（tw-command）。

## EMQX Cloud 配置要点

1. **Authentication → HTTP**：`POST http://<access-host>:5006/emqx/auth`  
2. **Authorization → HTTP**：`POST http://<access-host>:5006/emqx/acl`  
3. **Webhook**：`client.connected` / `client.disconnected` → `POST .../emqx/webhook`  
4. **Rule**：匹配 `tsp/+/up/gps`，HTTP Action → `POST .../emqx/bridge/gps`  
   Body 建议带：`topic`、`clientid`、`username`、`payload`（GPS JSON）

MQTT Topic 约定：`tsp/{vin}/up/gps`（前缀可用 `tw.access.topic-prefix` 改）。

GPS Value 与 mall-tw 一致，例如：

```json
{"vin":"TESTVIN001","lng":116.39,"lat":39.91,"speed":36.5,"gpsTime":"2026-09-26T17:00:01.000+08:00"}
```

## 鉴权模式

| 模式 | 配置 | 说明 |
|------|------|------|
| 开发旁路 | `tw.access.auth.dev-bypass=true` | 任意非空用户密码放行；VIN 取 clientId 或 `dev-default-vin` |
| 正式 | `dev-bypass=false` | 调 mall-tw `POST /extend/tw/device/verify` + bound-vin |

## 下行发布（Eclipse Paho）

按 [EMQX Cloud Java SDK](https://docs.emqx.com/zh/cloud/latest/connect_to_deployments/java_sdk.html) 用 **Paho** 长连接 EMQX，再 `publish` 到 `tsp/{vin}/down/cmd`。

```http
POST /tw/access/mqtt/publish
{"vin":"TESTVIN001","payload":"{\"cmd\":\"LOCK\"}","qos":1}
```

| 配置 | 说明 |
|------|------|
| `tw.access.mqtt.enabled=true` | 启动时连接 Broker |
| `tw.access.mqtt.broker` | `tcp://host:1883` 或 `ssl://host:8883`（Serverless 仅 TLS） |
| `tw.access.mqtt.username/password` | 平台账号（在 EMQX 认证中单独建，勿用车机号） |
| `tw.access.mqtt.ca-cert-path` | 单向 TLS 时 CA 路径（概览页下载） |

平台账号 ACL 需允许发布 `tsp/+/down/#`（或等价前缀）。

## 本地启动

```bash
# 依赖：Nacos、Redis、Kafka；mall-tw 可选（dev-bypass=true 时可先不启）
mvn -pl mall-tw-access -am -DskipTests package
java -jar mall-tw-access/target/mall-tw-access-*.jar
```

环境变量：`NACOS_HOST`、`NACOS_PASSWORD`、`KAFKA_BOOTSTRAP_SERVERS`；本地直连 mall-tw 时设 `MALL_TW_URL=http://127.0.0.1:5005`；下行设 `TW_ACCESS_MQTT_ENABLED`、`EMQX_MQTT_BROKER`、`EMQX_MQTT_USERNAME`、`EMQX_MQTT_PASSWORD`（TLS 再加 `EMQX_MQTT_CA_CERT`）。

## Sa-Token

本模块已放行 `/emqx/**`、`/actuator/**`。若 Nacos `sa-token.yaml` 另有全局拦截，请同样排除 `/emqx/**`。

## SkyWalking

启动挂载 SkyWalking Java Agent（与 tag `0.0.7` 一致），勿再使用 Elastic APM Agent：

```text
-javaagent:/path/to/skywalking-agent.jar
-Dskywalking.agent.service_name=mall-tw-access
-Dskywalking.collector.backend_service=<oap-host>:11800
```

日志 TID 字段为 `%X{tid}`（`TraceIdMDCPatternLogbackLayout`）。MQTT 上行由 `@Trace(operationName = "MQTT up/gps")` 建本地 Span。

## 网关样例

见 `docs/nacos/gateway-tw-access-route.yaml`。
