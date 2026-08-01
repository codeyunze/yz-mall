# mall-jaeger：OpenFeign 跨服务调用 + Jaeger 链路追踪 Demo

## 模块

| 模块 | 端口 | 职责 |
|---|---|---|
| `mall-jaeger-a` | 26101 | 入口；Feign 调用 B |
| `mall-jaeger-b` | 26102 | 被调用方 |

调用链：`HTTP /a/hello` → A → Feign → B `/b/hello`

推荐追踪方案：**OpenTelemetry Java Agent** → OTLP → Jaeger（下文）。  
工程里也保留了 Micrometer OTLP 配置；**Agent 与 Micrometer 二选一**，同时开容易出现重复 Span。

## 1. 启动 Jaeger

在 `docs/docker` 目录：

```bash
docker compose up -d jaeger
```

- UI：http://127.0.0.1:16686
- OTLP HTTP：`4318` / gRPC：`4317`

## 2. 配置 OpenTelemetry Agent（推荐）

Agent 路径：`E:\apply\opentelemetry\opentelemetry-javaagent.jar`

### IDEA（Run/Debug Configuration → VM options）

**mall-jaeger-a：**

```text
-javaagent:E:\apply\opentelemetry\opentelemetry-javaagent.jar
-Dotel.service.name=mall-jaeger-a
-Dotel.exporter.otlp.endpoint=http://127.0.0.1:4318
-Dotel.exporter.otlp.protocol=http/protobuf
-Dotel.metrics.exporter=none
-Dotel.logs.exporter=none
-Dotel.traces.exporter=otlp
```

**mall-jaeger-b：**（只改 `otel.service.name`）

```text
-javaagent:E:\apply\opentelemetry\opentelemetry-javaagent.jar
-Dotel.service.name=mall-jaeger-b
-Dotel.exporter.otlp.endpoint=http://127.0.0.1:4318
-Dotel.exporter.otlp.protocol=http/protobuf
-Dotel.metrics.exporter=none
-Dotel.logs.exporter=none
-Dotel.traces.exporter=otlp
```

Jaeger 在其他机器时，把 endpoint 改成例如：`http://192.168.3.237:4318`。  
注意：Agent 的 endpoint 填 **基址**（不要带 `/v1/traces`）。

### 命令行

```bat
java -javaagent:E:\apply\opentelemetry\opentelemetry-javaagent.jar ^
  -Dotel.service.name=mall-jaeger-b ^
  -Dotel.exporter.otlp.endpoint=http://127.0.0.1:4318 ^
  -Dotel.exporter.otlp.protocol=http/protobuf ^
  -Dotel.metrics.exporter=none ^
  -Dotel.logs.exporter=none ^
  -jar mall-jaeger-b.jar
```

### 使用 Agent 时建议关掉 Micrometer 导出（避免双上报）

在对应 `application.yaml`：

```yaml
management:
  tracing:
    enabled: false
```

## 3. 启动服务

先启 B，再启 A（均挂上上述 Agent）。

Feign 地址（A 的 `application.yaml`）：

```yaml
jaeger.b.url: http://127.0.0.1:26102
```

## 常见问题：A/B TraceId 不一致

原因：Feign 默认不会透传 `traceparent`，B 会新建一条 Trace。

处理：在 **mall-jaeger-a** 增加依赖 `feign-micrometer`，并保持：

```yaml
spring.cloud.openfeign.micrometer.enabled: true
management.tracing.propagation.type: W3C
```

重启 A/B 后再调 `/a/hello`，两边日志里的 TraceId 应相同。
