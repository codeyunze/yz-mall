# EMQX Cloud / 自建 EMQX 对接 mall-tw-access 速查

## 回调 URL（将 HOST 换成公网可达地址）

| 能力 | Method | URL |
|------|--------|-----|
| 认证 | POST | `http://HOST:5006/emqx/auth` |
| ACL | POST | `http://HOST:5006/emqx/acl` |
| 上下线 | POST | `http://HOST:5006/emqx/webhook` |
| GPS 桥接 | POST | `http://HOST:5006/emqx/bridge/gps` |

## Auth 请求示例（EMQX → access）

```json
{"clientid":"TESTVIN001","username":"device-001","password":"secret"}
```

成功响应：`{"result":"allow","is_superuser":false}`

## GPS Rule Body 示例

```json
{
  "topic": "tsp/TESTVIN001/up/gps",
  "clientid": "TESTVIN001",
  "username": "device-001",
  "payload": {
    "vin": "TESTVIN001",
    "lng": 116.397128,
    "lat": 39.916527,
    "speed": 36.5,
    "gpsTime": "2026-09-26T17:00:01.000+08:00"
  }
}
```

## 下行（Paho，平台侧）

access 用 Eclipse Paho 连部署（非 HTTP API），见 [Java 连接方式](https://docs.emqx.com/zh/cloud/latest/connect_to_deployments/java_sdk.html)。

```yaml
tw.access.mqtt.enabled: true
tw.access.mqtt.broker: ssl://<deployment>.emqx.cloud:8883   # 或 tcp://...:1883
tw.access.mqtt.username: <平台账号>
tw.access.mqtt.password: <密码>
tw.access.mqtt.ca-cert-path: /path/to/emqx-cloud-ca.crt     # Serverless / TLS 时
```

在 EMQX 为平台账号配置认证，并授权发布 `tsp/+/down/#`。

## 联调顺序

1. 启动 Kafka、Redis、mall-tw-access（可 `dev-bypass=true`；下行再开 `mqtt.enabled`）
2. 启动 mall-tw 且 `tw.telemetry.kafka.enabled=true`
3. EMQX 配好 Auth/ACL/Webhook/Rule + 平台 Paho 账号
4. MQTT 客户端连 EMQX，向 `tsp/TESTVIN001/up/gps` 发 GPS JSON
5. 查 `GET http://mall-tw:5005/tw/telemetry/latest?vin=TESTVIN001`
