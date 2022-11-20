网关服务启动命令中加如下指令，可打印请求信息；

```text
-Dreactor.netty.http.server.accessLogEnabled=true
```

打印信息如下：

```text
2022-11-19 22:38:11.223  INFO 14200 --- [ctor-http-nio-3] reactor.netty.http.server.AccessLog      : 0:0:0:0:0:0:0:1 - - [19/Nov/2022:22:38:10 +0800] "GET /api/demo/b/12333 HTTP/1.1" 200 44 20000 517 ms
```
