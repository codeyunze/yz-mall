# RustFS（对象存储）— 命名空间 mall

镜像：`registry.cn-guangzhou.aliyuncs.com/devyunze/rustfs:1.0.0-rc.5`

## 文件

| 文件 | 说明 |
|------|------|
| `pvc.yaml` | 数据卷 20Gi、日志卷 2Gi（local-path） |
| `configmap.yaml` | 非敏感环境变量 |
| `secret.yaml.example` | 访问密钥模板（勿提交真实密钥） |
| `deployment.yaml` | Deployment + init 目录权限 |
| `service.yaml` | LoadBalancer `9000`/`9001` |

集群内地址：

- API：`rustfs.mall.svc.cluster.local:9000`
- Console：`rustfs.mall.svc.cluster.local:9001`

## 部署

```bash
# 1. 确保 mall 命名空间存在，且已有 ACR 拉取凭证
sudo k3s kubectl get ns mall
sudo k3s kubectl -n mall get secret acr-gz

# 2. 创建密钥（推荐命令行，勿把明文提交仓库）
sudo k3s kubectl -n mall create secret generic rustfs-secret \
  --from-literal=access-key='你的AccessKey' \
  --from-literal=secret-key='你的SecretKey'

# 或：cp secret.yaml.example secret.yaml 改完后 apply

# 3. 应用清单
cd docs/k3s/apps/mall/rustfs
sudo k3s kubectl apply -f pvc.yaml
sudo k3s kubectl apply -f configmap.yaml
sudo k3s kubectl apply -f deployment.yaml
sudo k3s kubectl apply -f service.yaml

# 4. 等待就绪
sudo k3s kubectl -n mall get pods,svc,pvc -l app.kubernetes.io/name=rustfs -o wide
sudo k3s kubectl -n mall rollout status deploy/rustfs
```

## 验证

```bash
# 健康检查（把 EXTERNAL-IP 换成实际节点 IP）
curl -sS http://<EXTERNAL-IP>:9000/health
curl -sS http://<EXTERNAL-IP>:9000/health/ready

# 集群内
sudo k3s kubectl -n mall run curl-rustfs --rm -it --restart=Never \
  --image=registry.cn-guangzhou.aliyuncs.com/devyunze/busybox:1.36.1 -- \
  wget -qO- http://rustfs.mall.svc.cluster.local:9000/health
```

## 注意

1. 勿使用公开默认口令 `rustfsadmin` 对外暴露。
2. 若 PVC Pending，先按 `docs/k3s/部署RustFS.md` 修正 `local-path` helper 镜像。
3. 旧环境若仍在命名空间 `rustfs`，迁到 `mall` 前请确认数据备份与应用连接串切换。
