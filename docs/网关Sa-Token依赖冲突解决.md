# 网关 Sa-Token 依赖冲突解决方案

## 问题描述

启动 gateway 服务时出现以下错误：

```
The bean 'saTokenContextFilterForServlet', defined in class path resource 
[cn/dev33/satoken/spring/SaTokenContextRegister.class], could not be registered. 
A bean with that name has already been defined in class path resource 
[cn/dev33/satoken/reactor/spring/SaTokenContextRegister.class] and overriding is disabled.
```

## 问题原因

gateway 模块同时引入了两个 Sa-Token starter，它们都尝试注册相同的 bean：

1. **`sa-token-reactor-spring-boot3-starter`** - Reactor 响应式版本（gateway 直接引入）
   - 用于 Spring Cloud Gateway 的响应式环境
   - 注册 bean：`saTokenContextFilterForReactor`

2. **`sa-token-spring-boot3-starter`** - Servlet 版本（通过 `mall-auth-interface` 传递引入）
   - 用于传统的 Servlet 环境
   - 注册 bean：`saTokenContextFilterForServlet`

这两个 starter 都尝试注册 `saTokenContextFilterForServlet` bean，导致冲突。

## 解决方案

在 gateway 的 `pom.xml` 中，引入 `mall-auth-interface` 时排除 `sa-token-spring-boot3-starter` 依赖：

```xml
<!-- 认证接口模块，用于获取权限加载逻辑 -->
<dependency>
    <groupId>com.yz</groupId>
    <artifactId>mall-auth-interface</artifactId>
    <version>${project.version}</version>
    <exclusions>
        <!-- 排除 servlet 版本的 sa-token，gateway 使用 reactor 版本 -->
        <exclusion>
            <groupId>cn.dev33</groupId>
            <artifactId>sa-token-spring-boot3-starter</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 原理说明

### 为什么需要排除？

1. **Gateway 是响应式环境**：Spring Cloud Gateway 基于 WebFlux，使用响应式编程模型
2. **只需要 Reactor 版本**：gateway 只需要 `sa-token-reactor-spring-boot3-starter`，不需要 servlet 版本
3. **避免 Bean 冲突**：排除 servlet 版本后，只有 reactor 版本注册 bean，不会冲突

### 依赖关系

```
mall-gateway
  ├── sa-token-reactor-spring-boot3-starter (直接依赖) ✅
  └── mall-auth-interface
      └── sa-token-spring-boot3-starter (已排除) ❌
```

## 验证

修复后，启动 gateway 服务应该不再出现 bean 冲突错误。

可以通过以下方式验证：

1. **启动服务**：启动 gateway 服务，检查是否还有 bean 冲突错误
2. **查看日志**：确认只有 reactor 版本的 bean 被注册
3. **功能测试**：测试网关的认证和权限校验功能是否正常

## 注意事项

1. **其他服务不受影响**：这个修改只影响 gateway 模块，其他业务服务（如 mall-sys、mall-oms）仍然使用 servlet 版本的 sa-token
2. **功能完整性**：排除 servlet 版本不会影响 gateway 的功能，因为 gateway 只需要 reactor 版本
3. **依赖传递**：`mall-auth-interface` 中的其他依赖（如 `sa-token-redis-jackson`）仍然会被引入，这是正确的

## 相关文件

- `mall-gateway/pom.xml` - 已添加排除配置
- `mall-auth/mall-auth-interface/pom.xml` - 包含 `sa-token-spring-boot3-starter` 依赖

