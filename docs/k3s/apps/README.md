# k3s 应用清单目录

本目录存放可 `kubectl apply` 的 YAML 模板，与 [通用部署操作.md](../通用部署操作.md) 配套使用。

```text
apps/
├── mall-middleware/     # 中间件命名空间
│   ├── namespace.yaml
│   └── redis/           # 示例：Redis
└── mall/                # 业务微服务命名空间
    ├── namespace.yaml
    └── mall-sys/        # 示例：mall-sys
```

**注意：** `*.example` 文件需复制后改密码再使用；真实 Secret 不要提交 Git。
