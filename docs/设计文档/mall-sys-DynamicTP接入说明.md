# mall-sys 接入 Dynamic TP 说明

依据官方文档：

- [Nacos Cloud 接入](https://dynamictp.cn/guide/configcenter/nacos-cloud.html)
- [Prometheus + Grafana](https://dynamictp.cn/guide/monitor/prometheus_grafana.html)

## 业务 @Async 线程池

Bean 名：`mallSysAsyncExecutor`（`MallSysAsyncConfig.ASYNC_EXECUTOR`）

```java
@Async // 默认走 mallSysAsyncExecutor
public void doBiz() { ... }

@Async(MallSysAsyncConfig.ASYNC_EXECUTOR) // 显式指定
public void doBiz() { ... }
```

Nacos 中该池必须配置 `autoCreate: false`，由代码 `@Bean` + `@DynamicTp` 创建，Dynamic TP 只负责热更新与监控。


## 2. Nacos 配置

1. 打开 Nacos 控制台，选择与 mall-sys 相同的 Namespace  
2. 新建配置：  
   - Data ID：`mall-sys-dtp.yaml`  
   - Group：`DEFAULT_GROUP`  
   - 格式：YAML  
3. 内容复制仓库内样例：[mall-sys-dtp.yaml](../example/mall-sys-dtp.yaml)

验证热更新：把 `corePoolSize` / `maximumPoolSize` 改大或改小，发布配置后调用：

```bash
curl http://127.0.0.1:25002/sys/dtp/demo/info
```

应看到参数已变化。可再压任务：

```bash
curl -X POST "http://127.0.0.1:25002/sys/dtp/demo/submit?tasks=30&sleepMs=2000"
curl http://127.0.0.1:25002/sys/dtp/demo/info
```

## 3. Prometheus

1. 配置文件：`docs/docker/prometheus/prometheus.yml`（已增加 `mall-sys` job）  
2. 重启 Prometheus 容器使配置生效  
3. 浏览器打开 `http://<prometheus主机>:9090/targets`，确认 `mall-sys` 为 UP  
4. 指标页：`http://127.0.0.1:25002/actuator/prometheus`，搜索 `thread` / `dtp` 相关指标  

若 Prometheus 在 Docker、应用在宿主机，默认用 `host.docker.internal:25002`；不通时改为宿主机局域网 IP。

## 4. Grafana

1. 数据源指向 Prometheus  
2. 导入 Dynamic TP 官方面板（文档中的 Panel JSON，可向官方社群索取）  
3. 若无数据，检查每个 Panel 的数据源是否选中 Prometheus  

参考：[prometheus+grafana 接入流程](https://dynamictp.cn/guide/monitor/prometheus_grafana.html)
