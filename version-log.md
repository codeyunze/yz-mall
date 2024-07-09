# 系统变更记录

## 0.0.1-SNAPSHOT

### 新增
- 添加自定义业务异常以及全局异常捕获
- 添加商品管理模块
- 添加订单管理模块
- 商品管理模块拆分为interface、core、feign，实现可灵活集成功能模块

### 优化
- redis的序列化由默认的JdkSerializationRedisSerializer字节流格式存储，调整StringRedisSerializer文本字符串形式存储

### 修复