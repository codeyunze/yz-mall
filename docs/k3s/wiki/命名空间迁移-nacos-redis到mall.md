# 操作记录：Nacos / Redis / mall-tw-vehicle 迁入 `mall` 命名空间

| 项 | 内容 |
|---|---|
| 日期 | 2026-09-06 |
| 操作 | 新建 `mall`；迁入 Nacos、Redis；再迁入 `mall-tw-vehicle` |
| 状态 | 资源已迁入；旧 NS 对应 Deployment 已缩容为 0 |

## 迁移后访问地址

| 组件 | 旧 | 新 |
|---|---|---|
| Nacos DNS | `nacos.mall-middleware.svc.cluster.local` | `nacos.mall.svc.cluster.local` |
| Redis DNS | `redis.mall-middleware.svc.cluster.local` | `redis.mall.svc.cluster.local` |
| mall-tw-vehicle | `mall-business` | `mall`（Service：`mall-tw-vehicle.mall.svc.cluster.local:25005`） |
| Nacos NodePort | `30848` / `30849` | 不变 |
| Redis NodePort | `32223` | 不变 |

**ClusterIP 会变化**，禁止写死旧 IP；业务请用 DNS。

## 迁移后必查

1. Nacos 控制台配置（如 `middleware.yaml`）中的 Redis 等主机名改为 `*.mall.svc...`。  
2. `mall-tw-vehicle` 若 Ready 失败，先查应用日志（常见为缺 RocketMQ），与命名空间迁移无关。  
3. 确认无依赖后清理：

```bash
sudo k3s kubectl -n mall-middleware delete deploy nacos redis
sudo k3s kubectl -n mall-business delete deploy mall-tw-vehicle
# 空了再删 Namespace
# sudo k3s kubectl delete ns mall-middleware mall-business
```

## 相关文档

- [跨节点访问Nacos超时.md](./跨节点访问Nacos超时.md)
- 模板：`docs/k3s/apps/mall/nacos/`、`redis/`、`mall-tw-vehicle/`
