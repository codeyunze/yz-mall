# TSP 车联网云平台 — 设计文档索引

> **定位**：基于 yz-mall 技术栈的车联网（TSP / Telematics Service Platform）技术验证项目，用于简历展示与面试准备，**非生产真实运营系统**。  
> **复用**：登录、用户/角色、字典、菜单权限等基础能力直接复用 `yz-mall` + `yz-mall-web-admin-pc`。

## 文档清单

| 文档 | 说明 |
|------|------|
| [系统架构设计.md](./系统架构设计.md) | 技术栈、微服务划分、EMQX/双 MQ、数据与安全架构 |
| [功能模块规划.md](./功能模块规划.md) | 业务域拆分、功能清单、功能架构图、核心流程 |
| [开发优先级.md](./开发优先级.md) | MVP 优先路线：先跑通「车能上线、能看位置、能下控」 |

## 一句话目标

**车机通过 MQTT（EMQX）接入云端 → 遥测进 Kafka → 业务事件走 RocketMQ → 管理端可管车、看轨迹、下发远程指令。**

## 与电商系统的关系

```
yz-mall（已有）                    TSP 增量（本目录设计）
├── mall-gateway                  ├── tsp-vehicle   车辆档案
├── mall-auth                     ├── tsp-device    终端/证书/MQTT 凭证
├── mall-admin (sys)              ├── tsp-access   EMQX 接入与鉴权桥接
├── mall-file / mall-serial       ├── tsp-telemetry 遥测接入与查询
└── mall-utils                    ├── tsp-command  远程控制
                                  └── tsp-alarm    告警（第二阶段）
```

管理端在现有 `yz-mall-web-admin-pc` 上增加「车联网」菜单与页面即可，无需新建独立前端工程。
