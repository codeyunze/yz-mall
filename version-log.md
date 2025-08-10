# 系统变更记录

## 0.0.6-SNAPSHOT

### 优化升级
- JDK8升级到JDK17
- Spring Boot2.6.15升级到Spring Boot3.4.7
- spring-cloud-alibaba-dependencies依赖从2021.0.5.0升级到2022.0.0.2
- spring-cloud-dependencies依赖从2021.0.5.0升级到2024.0.2
- sa-token依赖从1.39.0升级到1.43.0
- redisson依赖从3.32.0升级到3.50.0
- dynamic-datasource从3.5.2升级到4.3.1
- mybatis-plus依赖从3.5.4升级到3.5.12
- mysql驱动依赖从8.0.33升级到8.4.0
- 优化mall-auth认证授权模块设计
- 优化业务架构模块设计


## 0.0.1-SNAPSHOT

### 新增
- 添加自定义业务异常以及全局异常捕获
- 添加商品管理模块
- 添加订单管理模块
- 添加系统基础功能模块（用户、组织、角色、菜单、权限管理等）
- 商品管理模块拆分为interface、core、feign，实现可灵活集成功能模块
- 集成Sa-Token权限认证框架、Resilience4j轻量级容错框架


### 优化
- redis的序列化由默认的JdkSerializationRedisSerializer字节流格式存储，调整StringRedisSerializer文本字符串形式存储

### 修复