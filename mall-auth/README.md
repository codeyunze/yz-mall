# 安全认证与授权服务

为其他服务模块提供访问权限授权校验、注册、登录、登出等功能

## 模块介绍

### mall-auth-interface(认证与授权扩展接口模块)

mall-auth-interface模块一般被需要认证与授权的服务模块引入，然后在其启动模块中引入mall-auth-feign模块。

### mall-auth-core(认证与授权核心功能实现模块)

mall-auth-core一般被引入到mall-sys模块里，是认证与授权的核心实现本体模块

### mall-auth-feign(认证与授权服务feign调用模块)

mall-auth-feign模块引入了mall-auth-interface模块的接口，其接口的实现为通过openfeign调用mall-auth-core模块的扩展接口。
