# 问题说明：跨节点访问 Nacos（ClusterIP）超时

| 项 | 内容 |
|---|---|
| 环境 | k3s `v1.32.1+k3s1`；master `vm-master-01`（内网 `10.0.0.5`）；worker `vm-worker-01`（内网 `10.0.4.12`） |
| 现象时间 | 2026-09-01 |
| 相关工作负载 | `mall-middleware/nacos`（跑在 master）；`mall-business/mall-tw-vehicle`（跑在 worker） |
| 状态 | 已定位并临时修复；需在 worker 持久化 FDB |

---

## 1. 现象

`mall-tw-vehicle` 等业务 Pod 使用：

```text
NACOS_HOST=10.43.242.61   # nacos Service ClusterIP
```

日志反复报错：

```text
[NacosClientAuthServiceImpl] login http request failed
url: http://10.43.242.61:8848/nacos/v1/auth/users/login
errorMsg: Connect timed out

Server check fail, please check server 10.43.242.61, port 9848 is available
java.util.concurrent.TimeoutException: ...
```

早期若配置 `NACOS_HOST=nacos.mall-middleware.svc.cluster.local`，还可能先出现 Java `UnknownHostException`（与本次「超时」不是同一层问题；超时阶段 DNS/IP 已可用）。

---

## 2. 常见误判（做了也不生效）

| 操作 | 为何无效 |
|---|---|
| 安全组对公网 IP 放行 **TCP 8848/9848** | 集群内访问走的是 **Pod 网段 + flannel VXLAN**，不是主机上的 8848 |
| 只改 Nacos Service / NodePort | Service 与 Endpoints 正常；问题在跨节点数据面 |
| 以为「开了 UDP 8472 就一定通」 | 8472 能进 worker，但 worker **发不出** VXLAN 回包时，业务仍超时 |

说明：NodePort（如 `10.0.0.5:30848`）能通，只说明 **主机网互通**，不能证明 **Pod 网（10.42.0.0/16）跨节点互通**。

---

## 3. 排查结论（对照实验）

在 worker 上跑 busybox 对比：

| 源 | 目标 | 结果 |
|---|---|---|
| worker 上的 Pod | `10.43.242.61:8848`（ClusterIP） | 超时 |
| worker 上的 Pod | `10.42.2.35:8848`（Nacos Pod IP，在 master） | 超时 |
| worker 上的 Pod | `10.0.0.5:30848`（NodePort） | **通** |
| master 上的 Pod | ClusterIP / Nacos Pod IP | **通** |

tcpdump：

- **master**：能看到发往 `10.0.4.12:8472` 的 VXLAN
- **worker**：能看到 `eth0 In` 的 VXLAN（安全组入站基本 OK）
- **worker**：长时间 **没有** 对应的 `Out` 回包

worker 上统计：

```text
ip -s link show flannel.1
# TX packets ≈ 0，TX dropped 持续增大（上千）
bridge fdb show dev flannel.1
# 空（没有到 master VTEP 的 FDB）
```

**根因：** worker 节点 `flannel.1` 缺少到 master 的 VXLAN FDB 表项，内核无法封装回程，表现为跨节点 Pod 网络单向/全断；业务访问 Nacos ClusterIP 即 Connect timed out。

master 侧 FDB 正常时类似：

```text
# master
bridge fdb show dev flannel.1
# 含：worker flannel MAC  dst 10.0.4.12
```

worker 修复前缺少：

```text
# 应为：master flannel MAC  dst 10.0.0.5
9a:e9:81:13:d0:29 dst 10.0.0.5 self permanent
```

> MAC 以各节点 `ip -d link show flannel.1` 为准，不要照抄过期值。

---

## 4. 修复方法

### 4.1 临时修复（立即恢复跨节点）

在 **vm-worker-01** 执行（MAC 先查 master）：

```bash
# 在 master 上看 flannel MAC
ip -d link show flannel.1 | head -5

# 在 worker 写入 FDB（示例 MAC，请替换为实际值）
bridge fdb replace <MASTER_FLANNEL_MAC> dev flannel.1 dst 10.0.0.5 self permanent
bridge fdb show dev flannel.1
```

验证：

```bash
# master
ping -c 3 10.42.1.0

# worker
ping -c 3 10.42.2.0
ping -c 3 <nacos-pod-ip>

# 任意节点
sudo k3s kubectl -n mall-business run net-w1 --rm -it --restart=Never \
  --image=registry.cn-guangzhou.aliyuncs.com/devyunze/busybox:1.36.1 \
  --overrides='{"spec":{"nodeName":"vm-worker-01"}}' \
  -- wget -O- -T5 http://10.43.242.61:8848/nacos/
```

能返回 Nacos 页面 HTML 即 ClusterIP 跨节点恢复。应用侧应出现类似：

```text
Success to connect a server [10.43.242.61:8848]
```

### 4.2 持久化（重启后 FDB 可能丢失）

在 **worker** 上：

```bash
# 将 <MASTER_FLANNEL_MAC> 换成实际值
cat >/usr/local/bin/flannel-fdb-master.sh <<'EOF'
#!/bin/bash
/sbin/bridge fdb replace <MASTER_FLANNEL_MAC> dev flannel.1 dst 10.0.0.5 self permanent
EOF
chmod +x /usr/local/bin/flannel-fdb-master.sh

# 编辑脚本填入真实 MAC 后：
cat >/etc/systemd/system/flannel-fdb-master.service <<'EOF'
[Unit]
Description=Ensure flannel FDB entry to k3s master
After=network-online.target
Wants=network-online.target

[Service]
Type=oneshot
ExecStart=/usr/local/bin/flannel-fdb-master.sh
RemainAfterExit=yes

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable --now flannel-fdb-master.service
```

也可在 `k3s-agent` 启动后由 cron/`ExecStartPost` 再执行一次该脚本。

### 4.3 业务临时绕过（网络未修好时）

将依赖 Nacos 的 Deployment 与 Nacos **调度到同一节点**（例如 master）：

```bash
sudo k3s kubectl -n mall-business patch deploy mall-tw-vehicle \
  --type='merge' \
  -p '{"spec":{"template":{"spec":{"nodeName":"vm-master-01"}}}'
```

同节点走本机 CNI，不依赖跨节点 VXLAN。网络恢复后删除 `nodeName`。

---

## 5. 安全组仍需保留的基线

即使本次根因是 FDB，跨节点 flannel 仍依赖：

| 方向 | 协议/端口 | 说明 |
|---|---|---|
| master ↔ worker（**内网 IP**） | **UDP 8472** | flannel VXLAN |
| master ↔ worker | TCP 10250 | kubelet（可选但建议） |

不要用「只对公网 IP 开 TCP 8848」代替上述规则。Nacos 的 8848/9848 是 **Service/容器端口**，不是 flannel 隧道端口。

---

## 6. 推荐排障清单（以后遇到同类问题）

1. 确认 Nacos Pod / Service / Endpoints 正常。  
2. 在 **业务所在节点** 与 **Nacos 所在节点** 分别用 busybox 测 ClusterIP、Pod IP、NodePort。  
3. 若仅跨节点失败：两端 `tcpdump -ni any udp port 8472`。  
4. 看 `ip -s link show flannel.1` 的 **TX dropped** 与 `bridge fdb show dev flannel.1`。  
5. FDB 缺失则补对端 `dst <peer-node-ip>`；再测 ClusterIP。  
6. 仍失败再查本机 `rp_filter`、firewalld/iptables、网卡 UDP tunnel offload。

---

## 7. 参考命令速查

```bash
# 节点与 Nacos 位置
sudo k3s kubectl get nodes -o wide
sudo k3s kubectl -n mall-middleware get pods,svc,endpoints -o wide

# flannel
ip -d link show flannel.1
ip -s link show flannel.1
bridge fdb show dev flannel.1
ip route | grep 10.42

# 抓隧道
sudo tcpdump -ni any udp port 8472 -c 20
```
