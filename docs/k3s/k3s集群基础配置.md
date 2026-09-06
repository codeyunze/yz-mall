# k3s 集群基础配置（ACR 鉴权 / Worker 加入 / Kuboard）

面向 yz-mall 环境：master 基础配置、新 Worker 加入、镜像与 Kuboard 接入。  
**账号密码、kubeconfig 只存服务器本地，禁止提交 Git。**

---

## 0. 机器与目录速查

| 机器 | 登录 | 内网 IP | 关键目录 / 文件 |
|---|---|---|---|
| **k3s master** | `ubuntu@111.229.156.123` | `10.0.0.5` | `/etc/rancher/k3s/registries.yaml`、`config.yaml`、`k3s.yaml` |
| **worker-01** | `root@101.43.84.238` | `10.0.4.12` | `/etc/rancher/k3s/registries.yaml`、`config.yaml` |
| **worker-02** | `root@43.138.193.177` | `10.0.20.10` | 同上 |
| **Kuboard** | 浏览器 `http://101.43.84.238:8081/` | — | 粘贴 kubeconfig |

ACR 仓库：`registry.cn-guangzhou.aliyuncs.com/devyunze/`  
k3s 版本（须一致）：`v1.32.1+k3s1`

配置改完后重启：

| 机器 | 命令 |
|---|---|
| master | `sudo systemctl restart k3s` |
| worker | `systemctl restart k3s-agent` |

---

## 1. 操作总览

### 1.1 新 Worker 加入（推荐顺序）

```text
master 准备（§2）
  → worker 前置检查（§3.1）
  → worker 下载 k3s 二进制（§3.2）
  → worker 安装 agent（§3.3）
  → worker 镜像配置（§3.4，必做）
  → master 验证（§6）
```

> **常见误区：** master 上已出现节点名 ≠ 节点 Ready。worker-02 实测：节点能 `Registered` 但长期 **NotReady**，原因是镜像未配齐，不是 Token 或网络问题。

### 1.2 必做步骤一览（每台 worker）

| 顺序 | 机器 | 目录 / 操作 |
|---|---|---|
| 1 | master | 安全组对新 worker 内网 IP 放行 6443/TCP、8472/UDP |
| 2 | worker | 停 Docker（§3.1） |
| 3 | worker | `wget` → `/usr/local/bin/k3s`（§3.2） |
| 4 | worker | `INSTALL_K3S_SKIP_DOWNLOAD=true` 装 agent（§3.3） |
| 5 | worker | `registries.yaml` 仅保留 ACR，**删掉**失效 docker.io mirror（§3.4.1） |
| 6 | worker | `config.yaml` 写入 `pause-image`（§3.4.2） |
| 7 | worker | 导入 `k3s-worker-base.tar`（含 klipper-lb，§3.4.3） |
| 8 | worker | **`systemctl restart k3s-agent` 一次**（§3.4.4） |
| 9 | master | `kubectl get nodes`；删该节点卡住的 Pod（§6） |

---

## 2. master 基础配置（加入 worker 前）

### 2.1 确认集群与 Token

**机器：** `ubuntu@111.229.156.123`  
**目录：** `/var/lib/rancher/k3s/server/node-token`

```bash
ssh ubuntu@111.229.156.123
sudo k3s kubectl get nodes
sudo k3s --version
sudo cat /var/lib/rancher/k3s/server/node-token   # 复制整段，勿提交 Git
```

### 2.2 安全组

**控制台：** master `111.229.156.123` 入站，对**每台新 worker 内网 IP** 放行：

| 端口 | 协议 | 来源示例 |
|---|---|---|
| 6443 | TCP | `10.0.4.12/32`（worker-01）、`10.0.20.10/32`（worker-02） |
| 8472 | UDP | 同上 |

worker 上自测：`curl -sk -m 8 https://10.0.0.5:6443/version` 返回 **401** 即通。

### 2.3 config.yaml（tls-san + pause-image）

**目录：** `/etc/rancher/k3s/config.yaml`

```bash
sudo tee /etc/rancher/k3s/config.yaml > /dev/null <<'EOF'
tls-san:
  - "10.0.0.5"
  - "111.229.156.123"
  - "vm-master-01"
pause-image: registry.cn-guangzhou.aliyuncs.com/devyunze/mirrored-pause:3.6
EOF
sudo systemctl restart k3s
```

### 2.4 registries.yaml（ACR 鉴权，禁止失效 mirror）

**目录：** `/etc/rancher/k3s/registries.yaml`

```yaml
configs:
  "registry.cn-guangzhou.aliyuncs.com":
    auth:
      username: "你的ACR用户名"
      password: "你的ACR密码"
```

> **禁止**保留 `mirrors.docker.io` 指向 `*.mirror.aliyuncs.com`（如 `r1ttmtle`），国内常 **403**，会导致 `klipper-lb`、`pause` 等系统镜像拉失败。

```bash
sudo chmod 600 /etc/rancher/k3s/registries.yaml
sudo systemctl restart k3s
sudo k3s crictl pull registry.cn-guangzhou.aliyuncs.com/devyunze/mirrored-pause:3.6
```

导出供 worker 拷贝：

```bash
sudo cp /etc/rancher/k3s/registries.yaml ~/registries.yaml
sudo chown ubuntu:ubuntu ~/registries.yaml
```

### 2.5 导出 worker 基础镜像包（加入前打好）

**目录：** master `/tmp/k3s-worker-base.tar`

```bash
sudo k3s ctr images export /tmp/k3s-worker-base.tar \
  docker.io/rancher/mirrored-pause:3.6 \
  docker.io/rancher/klipper-lb:v0.4.9
sudo chown ubuntu:ubuntu /tmp/k3s-worker-base.tar
```

含 `pause:3.6` 与 **`klipper-lb:v0.4.9`**（svclb Pod 必需，worker-02 实测缺此镜像会 NotReady）。

---

## 3. Worker 加入集群

### 3.1 前置检查

**机器：** 新 worker（以 `vm-worker-02` / `10.0.20.10` 为例）

```bash
hostnamectl set-hostname vm-worker-02
curl -sk -m 8 https://10.0.0.5:6443/version   # 期望 401

# k3s worker 节点不要跑 Docker（与 k3s 争内存/网络）
systemctl stop docker docker.socket 2>/dev/null
systemctl disable docker docker.socket 2>/dev/null
```

建议内存 **≥4GB**；仅 2GB 时可加 Swap，且业务 Pod 先 `cordon`（§6.2）。

### 3.2 下载 k3s 二进制

**目录：** 当前目录 → `/usr/local/bin/k3s`

```bash
wget https://rancher-mirror.rancher.cn/k3s/v1.32.1-k3s1/k3s -O k3s
chmod +x k3s
mv k3s /usr/local/bin/k3s
k3s --version
```

> GitHub 慢时不要干等安装脚本；先手动 `wget` 再 `INSTALL_K3S_SKIP_DOWNLOAD=true`。

### 3.3 安装 agent

**目录：** `/etc/rancher/k3s/`（可先 `mkdir -p`）

```bash
mkdir -p /etc/rancher/k3s

curl -sfL https://rancher-mirror.rancher.cn/k3s/k3s-install.sh | \
  INSTALL_K3S_SKIP_DOWNLOAD=true \
  INSTALL_K3S_VERSION="v1.32.1+k3s1" \
  K3S_URL="https://10.0.0.5:6443" \
  K3S_TOKEN="从master获取的node-token" \
  sh -s - agent
```

安装脚本停在 `Starting k3s-agent` 时：**可 Ctrl+C**，service 通常已装好。另开窗口查：

```bash
systemctl status k3s-agent
journalctl -u k3s-agent -n 30 --no-pager
```

master 上应能看到新节点（可能为 **NotReady**，继续 §3.4）：

```bash
sudo k3s kubectl get nodes -o wide
```

若节点记录异常需重装：master 上 `kubectl delete node <name>`，worker 上 `k3s-agent-uninstall.sh` 后重做 §3.2～3.4（保留 `/etc/rancher/k3s/` 下已配好的文件）。

### 3.4 加入后必做（镜像配置，顺序不可乱）

#### 3.4.1 registries.yaml

**机器：** worker → `/etc/rancher/k3s/registries.yaml`

```bash
scp ubuntu@111.229.156.123:~/registries.yaml /etc/rancher/k3s/registries.yaml
chmod 600 /etc/rancher/k3s/registries.yaml
```

**检查并删除失效 mirror**（worker-02 根因之一）：

```bash
grep mirror /etc/rancher/k3s/registries.yaml
# 不应出现 mirrors: / docker.io / r1ttmtle
# 若仍有，删掉 mirrors 整段，只保留 configs（ACR auth）
```

#### 3.4.2 config.yaml（pause-image）

**目录：** `/etc/rancher/k3s/config.yaml`

```bash
cat > /etc/rancher/k3s/config.yaml <<'EOF'
pause-image: registry.cn-guangzhou.aliyuncs.com/devyunze/mirrored-pause:3.6
EOF
k3s crictl pull registry.cn-guangzhou.aliyuncs.com/devyunze/mirrored-pause:3.6
```

> 仅 `crictl pull` **不够**，必须写 `config.yaml` 并 restart agent，否则仍拉 `docker.io/rancher/mirrored-pause:3.6`。

#### 3.4.3 导入 klipper-lb（svclb 必需）

**目录：** worker `/tmp/k3s-worker-base.tar`（来自 master §2.5）

```bash
scp ubuntu@111.229.156.123:/tmp/k3s-worker-base.tar /tmp/
k3s ctr images import /tmp/k3s-worker-base.tar
k3s crictl images | grep -E 'pause|klipper'
```

预期含 `klipper-lb:v0.4.9` 与 ACR `mirrored-pause:3.6`。

> tar 须完整（约 5MB）。经管道传输易损坏，优先 `scp` 二进制文件。

#### 3.4.4 重启 agent（只执行一次）

```bash
systemctl restart k3s-agent
sleep 30
systemctl status k3s-agent
journalctl -u k3s-agent -n 20 --no-pager
```

**不要**反复 `restart`，每次都会重新 bootstrap，易卡在 `activating` / `server is not ready`。

---

## 4. 业务镜像与 ACR

### 4.1 转存到 ACR（开发机或 CI）

| 源镜像 | ACR 镜像 |
|---|---|
| `rancher/mirrored-pause:3.6` | `.../devyunze/mirrored-pause:3.6` |
| `rancher/mirrored-library-busybox:1.36.1` | `.../devyunze/mirrored-library-busybox:1.36.1` |
| `rancher/klipper-lb:v0.4.9` | 建议转存 ACR，或继续用 §2.5 tar 导入 |
| `library/redis:7.4.5` | `.../devyunze/redis:7.4.5` |

### 4.2 imagePullSecrets（业务 Pod）

**机器：** master

```bash
sudo k3s kubectl create secret docker-registry acr-gz \
  -n mall-middleware \
  --docker-server=registry.cn-guangzhou.aliyuncs.com \
  --docker-username=你的ACR用户名 \
  --docker-password=你的ACR密码 \
  --dry-run=client -o yaml | sudo k3s kubectl apply -f -
```

Deployment 引用：`imagePullSecrets: [{ name: acr-gz }]`

### 4.3 local-path helper（PVC Pending，仅 master）

```bash
sudo k3s kubectl -n kube-system edit configmap local-path-config
# helperPod.yaml image 改为 ACR busybox
sudo k3s kubectl -n kube-system rollout restart deploy/local-path-provisioner
```

---

## 5. Kuboard 接入

| 机器 | 地址 |
|---|---|
| k3s API | `https://111.229.156.123:6443` 或内网 `https://10.0.0.5:6443` |
| Kuboard | `http://101.43.84.238:8081/` |

**master** 生成 kubeconfig（目录 `~/k3s-kuboard.yaml`）：

```bash
sudo cp /etc/rancher/k3s/k3s.yaml ~/k3s-kuboard.yaml
sudo chown "$USER:$USER" ~/k3s-kuboard.yaml
chmod 600 ~/k3s-kuboard.yaml
sed -i 's#https://127.0.0.1:6443#https://111.229.156.123:6443#' ~/k3s-kuboard.yaml
sed -i '/certificate-authority-data/d' ~/k3s-kuboard.yaml
sed -i '/server: https:\/\/111.229.156.123:6443/a\    insecure-skip-tls-verify: true' ~/k3s-kuboard.yaml
```

内网互通且保留 CA 时，将 `server` 改为 `https://10.0.0.5:6443`，勿加 `insecure-skip-tls-verify`。

**Kuboard 控制台：** 添加集群 → KubeConfig → 粘贴全文。安全组对 Kuboard IP 放行 master **6443/TCP**。

---

## 6. 验证与排障

### 6.1 验证（master）

```bash
sudo k3s kubectl get nodes -o wide
sudo k3s kubectl get pods -A -o wide
sudo k3s kubectl delete pod -n kube-system --field-selector spec.nodeName=vm-worker-02
```

预期：节点 **Ready**；worker 上 svclb **2/2 Running**；无 docker.io **403**。

### 6.2 低内存 worker（2GB）

```bash
# master：暂不调度业务
sudo k3s kubectl cordon vm-worker-02

# worker：可选加 Swap
fallocate -l 2G /swapfile && chmod 600 /swapfile && mkswap /swapfile && swapon /swapfile
```

### 6.3 常见现象对照（worker-02 实测）

| 现象 | 原因 | 处理 |
|---|---|---|
| master 有节点名但 **NotReady** | §3.4 镜像未配齐 | 按 §3.4.1～3.4.4 顺序做 |
| `klipper-lb` **403 Forbidden** | `registries.yaml` 含失效 mirror | 删 `mirrors` 段 + 导入 §2.5 tar |
| `Failed to create sandbox` + pause | 未写 `pause-image` | §3.4.2 |
| `systemctl` 长期 **activating** | 启动中或反复 restart | 等 2～3 分钟；勿连续 restart |
| `Waiting to retrieve agent configuration` | bootstrap 中或 Token 错 | 等或核对 Token；必要时 delete node 重装 |
| `127.0.0.1:6444` 超时 | 2GB 内存偏紧 | 停 Docker、加 Swap 或升配 |
| 安装脚本卡在 `Starting k3s-agent` | 脚本等待 service active | **Ctrl+C**，另窗口 `systemctl status` |
| 业务 Pod 只有 **Scheduled** | 节点 NotReady | 先修节点 Ready 再扩副本 |

---

## 7. 相关文档

- [通用部署操作.md](./通用部署操作.md) — 业务服务完整部署流程  
- [部署RustFS.md](./部署RustFS.md) — RustFS 镜像与 local-path 细节  
- [常用命令.md](./常用命令.md) — 日常 kubectl / 排障命令
