# docs 说明

## 架构文档

| 文档 | 说明 |
|------|------|
| [系统架构.md](./系统架构.md) | **现状架构**（基于源码：模块、分层、网关、认证、Feign、基建） |
| [yz-mall-架构图-风格10.svg](./yz-mall-架构图-风格10.svg) | 系统架构图（Style 10 Cloud Fabric，融合现状与设计规划，中文标注） |
| [系统架构设计.md](./系统架构设计.md) | 规划向设计稿（含后续扩展服务路线图，部分版本信息可能滞后） |
| [商品SKU订单购物车表结构设计建议.md](./商品SKU订单购物车表结构设计建议.md) | 基于存量表与真实电商模型的商品/SKU/库存/购物车/订单改造建议（含落地记录） |
| [日志系统设计方案.md](./日志系统设计方案.md) | 日志总线（Kafka）、运行/入站/出站三类日志、ES + SigNoz 多下游演进（替换 SkyWalking） |
| [sql/amount_unify_to_fen.sql](./sql/amount_unify_to_fen.sql) | 金额字段统一为分（bigint）：商品/订单/退款/用户余额 |
| [../README.md](../README.md) | 根 README：端口、分层示意图、权限与运维备注 |

## 脚本说明

| 脚本                                   | 说明                  |
|--------------------------------------|---------------------|
| EasyCodeConfig.json                  | easy code代码生成模板配置   |
| table-script.sql                     | 数据表创建脚本             |
| startup.bat                          | windows系统服务启动脚本     |
| startup.sh                           | linux系统服务启动脚本       |
| shutdown.sh                          | linux系统服务停止脚本       |
| dailybackup.sh                       | MySQL数据库备份脚本        |
| restorebackup.sh                     | MySQL备份数据还原脚本       |
| mall.pdma.json                       | PDManer元数建模脚本       |
| mysql-cluster-script数据库mysql集群脚本.sql | mysql主从同步配置命令       |
| ./script/docker/docker-compose.yml   | 开发环境中间件部署docker编排脚本 |


