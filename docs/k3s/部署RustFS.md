# 在 k3s 上部署 RustFS 中间件

本文记录在 yz-mall 运维环境（单节点 k3s）上部署 **RustFS**（对象存储兼容中间件）的操作步骤、镜像依赖与排障经验，以当前已落地环境为准。

## 1. 环境与目标形态

| 项 | 值（参考） |
|---|---|
| 主机 | `ubuntu@111.223.116.29` |
| k3s | `v1.32.1+k3s1`（单节点 control-plane） |
| 存储类 | `local-path`（默认，`WaitForFirstConsumer`） |
| Helm Release | `rustfs` / 命名空间 `rustfs` |
| Chart | `rustfs-0.12.0`（`app.kubernetes.io/version` 标注为 `1.0.0-beta.12`） |
| 业务镜像 | `registry.cn-guangzhou.aliyuncs.com/devyunze/rustfs:1.0.0-rc.2` |
| Service | `rustfs-svc`，类型 `LoadBalancer`，端口 `9000`（API）/ `9001`（Console） |
| 数据卷 | PVC `rustfs-data` 20Gi、`rustfs-logs` 2Gi |

部署成功后，集群内大致关系如下：

```text
Deployment/rustfs
  ├─ initContainer: busybox（准备 /data、/logs 目录）
  ├─ container: rustfs（:9000 / :9001）
  ├─ PVC rustfs-data → local-path
  └─ PVC rustfs-logs → local-path
Service/rustfs-svc (LoadBalancer) → 节点 IP:9000/9001
```

## 2. 前置条件

1. 节点已安装并运行 k3s：`sudo systemctl status k3s`
2. 默认可执行：`sudo k3s kubectl get nodes`（节点 Ready）
3. 有可用的阿里云广州 ACR 仓库：`registry.cn-guangzhou.aliyuncs.com/devyunze`
4. 本机或 CI（如 GitHub Actions）能把 Docker Hub 镜像转存到 ACR（国内节点常无法直连 Docker Hub）
5. （推荐）配置 k3s 私有仓认证：`/etc/rancher/k3s/registries.yaml`，改完后 `sudo systemctl restart k3s`  
   - **禁止**把账号密码写入本仓库文档；仅用环境变量 / 服务器本地文件管理

### 2.1 必须提前准备的镜像

| 用途 | ACR 镜像 | 说明 |
|---|---|---|
| RustFS 主程序 | `registry.cn-guangzhou.aliyuncs.com/devyunze/rustfs:1.0.0-rc.2` | Helm/Deployment 主容器 |
| init 容器 | `registry.cn-guangzhou.aliyuncs.com/devyunze/busybox:1.36.1` | Chart 默认常为 `busybox:stable`，建议改固定版本并走 ACR |
| local-path helper | `registry.cn-guangzhou.aliyuncs.com/devyunze/mirrored-library-busybox:1.36.1` | 创建 PVC 目录用；默认 `rancher/mirrored-library-busybox:1.36.1` 依赖 Docker Hub |

> 源镜像对应关系：`docker.io/library/busybox:1.36.1`、`docker.io/rancher/mirrored-library-busybox:1.36.1`。  
> 官网「Supported tags」可能不突出 `stable`，**请用固定版本 `1.36.1`**。

节点上预拉（可选，便于排障）：

```bash
sudo k3s crictl pull registry.cn-guangzhou.aliyuncs.com/devyunze/rustfs:1.0.0-rc.2
sudo k3s crictl pull registry.cn-guangzhou.aliyuncs.com/devyunze/busybox:1.36.1
sudo k3s crictl pull registry.cn-guangzhou.aliyuncs.com/devyunze/mirrored-library-busybox:1.36.1
```

### 2.2 修正 local-path helper 镜像（强烈建议先做）

若不改 helper 镜像，且节点无法访问 Docker Hub / 加速器 403，会出现：

- PVC `Pending`，事件：`create process timeout after 120 seconds`
- Pod `Pending`，事件：`VolumeBinding ... context deadline exceeded`
- provisioner 日志：`helper-pod ... trying and failing to pull image`

将 `kube-system/local-path-config` 中 `helperPod.yaml` 的 image 改为 ACR：

```yaml
image: "registry.cn-guangzhou.aliyuncs.com/devyunze/mirrored-library-busybox:1.36.1"
imagePullPolicy: IfNotPresent
```

然后重启 provisioner：

```bash
sudo k3s kubectl -n kube-system rollout restart deploy/local-path-provisioner
sudo k3s kubectl -n kube-system rollout status deploy/local-path-provisioner
```

> 注意：该 ConfigMap 由 k3s Addon 管理，**升级/重装 k3s 后可能被覆盖**，需重新修改或做成自动化补丁。

### 2.3 ACR 拉取凭证（imagePullSecret）

Deployment 中声明了：

```yaml
imagePullSecrets:
  - name: acr-gz
```

请在命名空间 `rustfs` 中创建同名 Secret（类型 `kubernetes.io/dockerconfigjson`）。  
若 Secret 缺失，界面/事件可能出现 `FailedToRetrieveImagePullSecret`；镜像已缓存在节点时仍可能启动成功，但**新建节点或删镜像后会失败**。

示例（密码用环境变量，勿写进 Git）：

```bash
sudo k3s kubectl -n rustfs create secret docker-registry acr-gz \
  --docker-server=registry.cn-guangzhou.aliyuncs.com \
  --docker-username="$ACR_USER" \
  --docker-password="$ACR_PASSWORD"
```

## 3. 部署步骤（Helm）

以下为可复现的安装流程；参数与当前集群落地结果对齐。若 Chart 仓库地址有变，以你们内部 Chart 源为准。

### 3.1 创建命名空间

```bash
sudo k3s kubectl create namespace rustfs
```

### 3.2 准备 values（示例）

将敏感项（访问密钥等）放到 Secret / 私有 values 文件，**不要提交仓库**。业务侧常见配置键（ConfigMap `rustfs-config`）：

| 配置键 | 当前值含义 |
|---|---|
| `RUSTFS_ADDRESS` | `:9000` |
| `RUSTFS_CONSOLE_ADDRESS` | `:9001` |
| `RUSTFS_CONSOLE_ENABLE` | `true` |
| `RUSTFS_VOLUMES` | `/data` |
| `RUSTFS_OBS_LOG_DIRECTORY` | `/logs` |
| `RUSTFS_OBS_LOGGER_LEVEL` | `info` |
| `RUSTFS_OBS_ENVIRONMENT` | `development` |
| `RUSTFS_REGION` | `us-east-1` |

密钥在 Secret `rustfs-secret`（字段名以 Chart 为准），本文不展开明文。

`values-rustfs.yaml` 示例骨架（按 Chart 实际字段名微调）：

```yaml
image:
  repository: registry.cn-guangzhou.aliyuncs.com/devyunze/rustfs
  tag: 1.0.0-rc.2
  pullPolicy: IfNotPresent

imagePullSecrets:
  - name: acr-gz

# init 容器改为 ACR busybox（字段名以 Chart 为准，也可安装后 kubectl set image）
# initContainer:
#   image: registry.cn-guangzhou.aliyuncs.com/devyunze/busybox:1.36.1

persistence:
  data:
    enabled: true
    storageClass: local-path
    size: 20Gi
  logs:
    enabled: true
    storageClass: local-path
    size: 2Gi

service:
  type: LoadBalancer
  # 端口一般为 9000 / 9001
```

### 3.3 安装 / 升级

```bash
# Chart 来源按实际替换：本地目录、OCI 或公司 ChartMuseum
helm upgrade --install rustfs <CHART_REF> \
  --namespace rustfs \
  --create-namespace \
  -f values-rustfs.yaml
```

无 Helm 客户端、仅用 k3s 时，也可用官方/自定义的 `helm-install-*` Job 方式安装（本环境曾出现 `helm-install-rustfs-*` Completed Job）。

### 3.4 安装后强制改 init 镜像（若 Chart 仍写 busybox:stable）

```bash
sudo k3s kubectl -n rustfs set image deploy/rustfs \
  init-step=registry.cn-guangzhou.aliyuncs.com/devyunze/busybox:1.36.1
```

## 4. 验证

```bash
# 资源总览
sudo k3s kubectl -n rustfs get pods,svc,pvc,deploy -o wide

# 期望：
# Pod Ready 1/1 Running
# PVC rustfs-data / rustfs-logs 均为 Bound
# Service EXTERNAL-IP 为节点内网 IP（本环境为 10.0.0.5）

# 健康检查（节点本机，NodePort 以实际为准）
sudo k3s kubectl -n rustfs get svc rustfs-svc
curl -sS http://127.0.0.1:<9000对应NodePort>/health
curl -sS http://127.0.0.1:<9000对应NodePort>/health/ready
```

健康响应示例：

```json
{"status":"ok","ready":true,"service":"rustfs-endpoint","version":"1.0.0-rc.2"}
```

本环境访问参考：

| 入口 | 地址 |
|---|---|
| API（集群 LB） | `http://10.0.0.5:9000` |
| Console | `http://10.0.0.5:9001`（未登录可能返回 403） |
| Kubernetes Dashboard | `http://111.223.116.29:9090/`（命名空间选 **rustfs** 或 All namespaces） |

> Dashboard 默认命名空间不是 `rustfs` 时，Pods 列表看不到 RustFS，属正常现象。

## 5. 日常运维命令

```bash
# 日志
sudo k3s kubectl -n rustfs logs -l app.kubernetes.io/name=rustfs -f --tail=200

# 重启
sudo k3s kubectl -n rustfs rollout restart deploy/rustfs
sudo k3s kubectl -n rustfs rollout status deploy/rustfs

# 描述排障
sudo k3s kubectl -n rustfs describe pod -l app.kubernetes.io/name=rustfs
sudo k3s kubectl -n rustfs describe pvc
sudo k3s kubectl -n kube-system logs -l app=local-path-provisioner --tail=100
```

更多通用命令见同目录 [常用命令.md](./常用命令.md)。

## 6. 常见问题

### 6.1 Pod 一直 Pending，PVC 也是 Pending

**原因**：`local-path` 创建卷依赖 helper 镜像，Docker Hub / 失效的阿里云加速器导致拉镜像失败。  

**处理**：按 §2.1、§2.2 转存并改 helper 镜像，重启 `local-path-provisioner`，必要时删除重建 Pending 的 Pod 以重新触发绑定。

### 6.2 init 容器 ImagePullBackOff

**原因**：仍使用 `busybox:stable` 且节点拉不到 Docker Hub。  

**处理**：改用 ACR `busybox:1.36.1`，并确保 `acr-gz` Secret 存在或镜像已预拉。

### 6.3 Dashboard（:9090）看不到 rustfs

切换 Namespace 到 `rustfs` 或 All namespaces。该界面是宿主机上的 Kubernetes Dashboard，不是 k3s 内置页面。

### 6.4 helper 镜像改回去了

k3s 升级后 Addon 可能重置 `local-path-config`。重新执行 §2.2，或把补丁纳入安装脚本。

## 7. 卸载（慎用）

```bash
helm uninstall rustfs -n rustfs
# PVC 若带 helm.sh/resource-policy: keep，可能需手动删除（会丢数据）
sudo k3s kubectl -n rustfs get pvc
# sudo k3s kubectl -n rustfs delete pvc rustfs-data rustfs-logs
# sudo k3s kubectl delete ns rustfs
```

删除 PVC / 命名空间前务必确认数据已备份。

## 8. 落地检查清单

- [ ] ACR 已具备 rustfs / busybox / mirrored-library-busybox 对应 tag  
- [ ] `local-path` helper 已改为 ACR 镜像，provisioner 已重启  
- [ ] `rustfs` 命名空间存在 `acr-gz`（或等价）拉取凭证  
- [ ] Helm 安装成功，`deploy/rustfs` Available  
- [ ] 两个 PVC Bound，Pod 1/1 Ready  
- [ ] `/health`、`/health/ready` 返回 200  
- [ ] Dashboard 在命名空间 `rustfs` 下可见 Pod  

## 9. 相关文档

- [k3s 运维文档索引](./README.md)
- [k3s 常用命令](./常用命令.md)
