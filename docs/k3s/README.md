# k3s 运维文档

本目录记录 yz-mall 相关环境中 **k3s** 的常用操作说明，面向日常排障与发布，不替代官方完整手册。

## 当前环境（参考）

| 项 | 说明 |
|---|---|
| 主机 | `ubuntu@111.223.116.29`（`VM-0-5-ubuntu`） |
| 发行版 | Ubuntu 24.04 |
| k3s 版本 | `v1.32.1+k3s1` |
| 角色 | 单节点 control-plane / master |
| 默认存储类 | `local-path`（WaitForFirstConsumer） |
| Ingress | Traefik（k3s 自带） |

> 主机 IP / 版本以实际机器为准；变更后请同步改本表。

## 文档索引

| 文档 | 说明 |
|---|---|
| [常用命令.md](./常用命令.md) | 服务启停、集群巡检、工作负载、日志、存储、镜像与排障等常用命令 |
| [通用部署操作.md](./通用部署操作.md) | k3s 部署中间件与 Spring Cloud 微服务的通用步骤、约定与排障 |
| [部署RustFS.md](./部署RustFS.md) | 在 k3s 上部署 RustFS：镜像转存、local-path、Helm、验证与排障 |
| [中间件迁移方案.md](./中间件迁移方案.md) | docs/docker/docker-compose.yml 迁到 k3s 的清单、批次与约束 |

## 使用前提

1. 本机已配置可免密登录的 SSH 密钥（或已能登录服务器）。
2. 在服务器上执行时，优先使用：`sudo k3s kubectl ...`（不依赖额外 kubeconfig）。
3. 也可复制 kubeconfig 后使用本机 `kubectl`（见《常用命令》）。

## 安全约定

- 禁止把 kubeconfig、Token、镜像仓库密码等明文提交到仓库。
- 生产变更（删除命名空间、强制删 Pod、改存储）前先 `get` / `describe` 确认对象。
