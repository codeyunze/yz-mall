# Notebook

<p align="center">
    <a href="#"><img src="https://img.shields.io/badge/作者-yunze-orange.svg" alt="作者"></a>
    <a href="#"><img src="https://img.shields.io/badge/前端项目-gitee地址-orange.svg" alt="前端项目"></a>
</p>

# 框架版本

| 组件                   | 版本号           |
|----------------------|---------------|
| Spring Boot          | 2.3.2.RELEASE |
| Spring Cloud         | Hoxton.SR9    |
| Spring Cloud Alibaba | 2.2.6.RELEASE |


# 模块说明

## base-gateway网关服务

IDEA服务启动项的`Add VM options`加上`-Dreactor.netty.http.server.accessLogEnabled=true`命令,可以打印访问日志

通过gateway服务访问base-search服务接口案例如下

```http request
GET http://localhost:20000/search/systaskjobhistory/list
X-Request-Id: 123
token: 666666
```