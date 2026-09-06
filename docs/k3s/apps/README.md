# k3s 应用清单目录

本目录存放可 `kubectl apply` 的 YAML 模板，与 [通用部署操作.md](../通用部署操作.md) 配套使用。

```text
apps/
├── mall/                    # 业务 + 中间件（Nacos/Redis 已迁入本 NS）
│   ├── namespace.yaml
│   ├── nacos/
│   ├── redis/
│   ├── mall-sys/
│   └── mall-tw-vehicle/
└── mall-middleware/         # 历史目录；Nacos/Redis 已迁到 mall，仅作参考
    ├── namespace.yaml
    └── redis/
```

集群内地址（迁移后）：

| 组件 | DNS |
|---|---|
| Nacos | `nacos.mall.svc.cluster.local:8848` |
| Redis | `redis.mall.svc.cluster.local:6379` |

**注意：** `*.example` 文件需复制后改密码再使用；真实 Secret 不要提交 Git。
