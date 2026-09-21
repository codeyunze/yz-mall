# mall-utils

yz-mall **公共能力**聚合模块（`packaging=pom`）。供各业务服务依赖，**禁止**在此编写具体业务逻辑。

版本跟随根工程 `yz-mall`（当前 `0.0.8-SNAPSHOT`）。

## 子模块一览

| 模块 | 职责 |
|---|---|
| `mall-json` | Jackson 等 JSON 序列化基础依赖与统一配置 |
| `mall-base` | 统一响应（`Result` / `ResultTable`）、分页、异常、校验等基类 |
| `mall-mybatis` | MyBatis-Plus、数据源相关 starter 封装 |
| `mall-redis` | Redis / Redisson 封装 |
| `mall-web` | Web 增强（AOP、OpenFeign 公共配置、请求头/追踪等） |
| `mall-rocketmq` | RocketMQ 生产消费公共封装 |
| `mall-job` | XXL-JOB 执行器公共配置；含 Elastic APM 任务链路辅助 |
| `mall-serial` | 流水号/序号服务（含 interface / core / feign，可独立或内嵌使用） |

## 依赖关系（简图）

```text
mall-json
   ↑
mall-base ──→ mall-web ←── mall-redis
   ↑              ↑
mall-mybatis     （业务 startup / core 常同时引入 mall-web + mall-redis）

mall-rocketmq、mall-job：按需引入
mall-serial：按「本地 core」或「远程 feign」选型引入
```

## 使用方式

业务模块在自身 `pom.xml` 中按需声明，例如：

```xml
<dependency>
    <groupId>com.yz</groupId>
    <artifactId>mall-web</artifactId>
    <version>${project.version}</version>
</dependency>
<dependency>
    <groupId>com.yz</groupId>
    <artifactId>mall-redis</artifactId>
    <version>${project.version}</version>
</dependency>
```

- 需要持久化：引入 `mall-mybatis`（通常再配动态数据源）
- 需要统一返回体 / 业务异常：引入 `mall-base`（多数经 `mall-web` 间接获得）
- 需要定时任务：引入 `mall-job`，业务侧实现 XXL-JOB Handler
- 需要序号：优先看是否已有 `mall-serial` 能力，避免各域自造轮子

## 边界

| 可以放 | 不要放 |
|---|---|
| 多域复用的技术组件、约定、自动配置 | 订单 / 商品 / 车辆等业务实体与接口 |
| 与中间件相关的薄封装 | 仅某一服务使用的 Controller / Service |

新增公共能力时：先确认是否已有同类封装；新 artifact 须在本聚合 `pom.xml` 的 `<modules>` 中登记，版本由父 POM 统一管理。
