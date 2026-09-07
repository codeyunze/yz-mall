# mall-tw

Titan Watch（车辆与终端）业务服务。由原 `mall-tw-vehicle`、`mall-tw-device` 合并为**单一可运行模块**。

| 项 | 说明 |
|---|---|
| 服务名 | `mall-tw`（`spring.application.name`） |
| 本地端口 | `25005` |
| 启动类 | `com.yz.mall.tw.TwApplication` |
| 包根 | `com.yz.mall.tw` |

## 职责范围

- **车辆档案**：车辆主数据、车主绑定/过户、授权用户、车系/车型
- **终端管理**：终端注册、启停、MQTT 凭证重置、终端与车辆绑定/解绑
- **跨服务扩展接口**（`extend/tw/**`）：供其他服务查询鉴权信息、车辆访问权限等

不包含：MQTT Broker 接入、轨迹入库、远程控车指令下发（由后续接入/遥测/指令域承担）。

## 包结构（按层组织）

```text
com.yz.mall.tw/
├── TwApplication / TwConfig
├── controller/     # 管理端 API + Extend API
├── service/        # 业务与 Extend 接口
├── service.impl/
├── dto/ / vo/ / entity/ / mapper/
├── constant/ / support/ / config/
```

类名仍用 `TwDevice*` / `TwVehicle*` 前缀区分域概念；HTTP 路径按域划分，互不冲突。

## 主要 API 前缀

| 前缀 | 说明 |
|---|---|
| `/tw/vehicle/**` | 车辆档案 |
| `/tw/series/**`、`/tw/model/**` | 车系 / 车型 |
| `/tw/device/**` | 终端管理 |
| `/extend/tw/vehicle/**` | 车辆扩展（如 by-vin、access/check） |
| `/extend/tw/model/**` | 车型扩展 |
| `/extend/tw/device/**` | 终端扩展（如 auth、by-vin、verify） |

经网关时统一转发到 `lb://mall-tw`，示例见 `docs/nacos/gateway-tw-route.yaml`。

## 配置与资源

- `src/main/resources/application.yaml`：端口、应用名、`tw.vehicle.max-auth-users` 等
- `bootstrap.yaml`：Nacos 配置/发现
- `src/main/resources/db/`：建表 SQL 与权限菜单参考文本

## 本地启动

```bash
# 在 yz-mall 根目录
mvn -pl mall-tw -am -DskipTests package
java -jar mall-tw/target/mall-tw-*.jar
```

依赖 Nacos（及中间件配置中的 MySQL、Redis 等）。部署清单见 `docs/k3s/apps/mall/mall-tw/`。

## 约定

- **禁止**向本模块塞入通用工具能力（应放 `mall-utils`）
- 鉴权与其它业务服务一致：管理接口使用 `@SaCheckPermission`
- 进程内车辆与终端互调走本地 `service`，不再拆 Feign 子模块
