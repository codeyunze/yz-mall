# 动态时间滑动窗口功能实现文档

## 一、功能概述

本模块实现了**基于 Redis 的动态时间滑动窗口限流功能**，主要用于限制客户端在指定时间窗口内操作的 UserId 数量。该功能可有效防止恶意请求或高频操作对系统造成的压力。

### 核心特性

| 特性 | 说明 |
|------|------|
| **动态窗口配置** | 支持通过请求参数动态配置时间窗口大小 |
| **UserId 去重计数** | 基于不重复 UserId 数量进行限流，同一 UserId 重复操作不计入 |
| **原子性保证** | 使用 Lua 脚本确保 Redis 操作的原子性 |
| **自动清理过期数据** | 定期清理滑动窗口外的过期记录 |
| **灵活的 URI 匹配** | 支持 Ant 风格的 URI 模式匹配，按需拦截 |

---

## 二、架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        请求层                                   │
│  HTTP Request → ControlRangeFilter → UserIdSlidingWindowGuardFilter│
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        业务层                                   │
│              UserIdSlidingWindowGateService                        │
│         (参数校验、UserId 去重、调用限流服务)                        │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        限流层                                   │
│              SlidingWindowUserIdLimiter                            │
│         (加载并执行 Lua 脚本，操作 Redis ZSET)                    │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        存储层                                   │
│                         Redis ZSET                              │
│               Key: sw:userId:{clientId}                           │
│               Member: UserId, Score: 时间戳(ms)                    │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 核心组件职责

| 组件 | 职责 | 所属文件 |
|------|------|----------|
| **UserIdSlidingWindowGuardFilter** | 请求拦截、参数提取、调用限流服务 | `web/UserIdSlidingWindowGuardFilter.java` |
| **UserIdSlidingWindowGateService** | 业务逻辑处理、UserId 规范化、参数校验 | `ratelimit/UserIdSlidingWindowGateService.java` |
| **SlidingWindowUserIdLimiter** | Lua 脚本加载与执行、Redis 交互 | `ratelimit/SlidingWindowUserIdLimiter.java` |
| **SwUserIdGuardProperties** | 配置属性定义、过滤器开关控制 | `guard/SwUserIdGuardProperties.java` |
| **UserIdSlidingWindowGuardConfiguration** | Spring Bean 注册、过滤器顺序控制 | `config/UserIdSlidingWindowGuardConfiguration.java` |

---

## 三、核心实现原理

### 3.1 滑动窗口算法原理

滑动窗口限流的核心思想是：在一个**动态移动的时间窗口**内，统计某个客户端操作的不重复 UserId 数量，超过阈值则拒绝请求。

**时间窗口示意图：**

```
时间轴 →
      [=======================]  窗口大小: windowMs
      ↑                       ↑
    左边界                  右边界(当前时间)
    cut = now - windowMs      now

窗口内有效记录：score > cut
窗口外过期记录：score <= cut (需清理)
```

### 3.2 Redis ZSET 数据结构设计

| 字段 | 说明 |
|------|------|
| **Key** | `sw:userId:{clientId}` - 客户端唯一标识 |
| **Member** | UserId |
| **Score** | 该 UserId 最近一次出现的时间戳（毫秒） |

### 3.3 Lua 脚本核心逻辑

Lua 脚本 `sliding-window-user-id-limit.lua` 是实现的核心，保证了操作的原子性：

```lua
-- 1. 计算窗口左边界
local cut = now - windowMs

-- 2. 清理过期记录（ZREMRANGEBYSCORE）
redis.call('ZREMRANGEBYSCORE', key, '-inf', cut - 1)

-- 3. 获取当前窗口内 UserId 数量
local current = redis.call('ZCARD', key)

-- 4. 统计本次新增 UserId 数量
local newCount = 0
for i = 5, #ARGV do
    local userId = ARGV[i]
    if redis.call('ZSCORE', key, userId) == false then
        newCount = newCount + 1
    end
end

-- 5. 判断是否超限
if current + newCount > maxDistinct then
    return 0  -- 拒绝
end

-- 6. 更新/插入 UserId 记录
for userId, _ in pairs(seen) do
    redis.call('ZADD', key, now, userId)
end

return 1  -- 允许
```

**脚本参数说明：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `KEYS[1]` | String | Redis ZSET Key |
| `ARGV[1]` | Long | 当前时间戳（毫秒） |
| `ARGV[2]` | Long | 滑动窗口长度（毫秒） |
| `ARGV[3]` | Int | 窗口内最大不重复 UserId 数 |
| `ARGV[4]` | Int | Key 的 TTL（秒） |
| `ARGV[5...]` | String | 本次请求的 UserId 列表 |

---

## 四、关键代码解析

### 4.1 请求过滤流程

`UserIdSlidingWindowGuardFilter.doFilterInternal()` 方法：

```java
// 1. 包装请求体（支持多次读取）
CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(request);

// 2. 提取客户端标识和用户标识
String clientId = wrapped.getHeader("clientId");
String userId = wrapped.getHeader("userId");

// 3. 获取客户端配置（从缓存读取）
ClientConfigVo clientConfig = getClientConfig(clientId);
int maxUserIdCount = clientConfig.getMaxUserIdCount();
int timeWindow = clientConfig.getTimeWindow();

// 4. 调用限流服务
userIdGate.assertWithinUserIdWindow(clientId.strip(), 
    Collections.singletonList(userId), maxUserIdCount, timeWindow);
```

### 4.2 限流服务逻辑

`UserIdSlidingWindowGateService.assertWithinUserIdWindow()` 方法：

```java
// 参数校验
if (clientId == null || clientId.isBlank()) {
    throw new ResponseStatusException(BAD_REQUEST, "clientId 不能为空");
}
if (maxUserIdCount <= 0 || timeWindow <= 0) {
    throw new ResponseStatusException(BAD_REQUEST, "参数须为正整数");
}

// UserId 去重处理
List<String> userIdList = normalizeUserIdList(userIds);

// 构建 Redis Key
String redisKey = REDIS_USER_ID_KEY_PREFIX + clientId;

// 计算实际限流阈值（受服务端上限约束）
int effectiveMaxUserIdCount = Math.min(maxUserIdCount, MAX_DISTINCT_USER_IDS_CAP);

// 执行限流判断
boolean ok = userIdLimiter.tryRecordDistinctUserIds(redisKey, userIdList, 
    effectiveMaxUserIdCount, windowMs);

if (!ok) {
    throw new ResponseStatusException(TOO_MANY_REQUESTS, 
        "滑动窗口内该 client 操作的 UserId 数量超过上限");
}
```

**关键设计点：**

| 设计点 | 实现方式 | 目的 |
|--------|----------|------|
| **服务端上限保护** | `Math.min(maxUserIdCount, MAX_DISTINCT_USER_IDS_CAP)` | 防止配置过大导致资源耗尽 |
| **UserId 规范化** | `normalizeUserIdList()` 去空、去重 | 保证数据一致性 |
| **异常处理** | `ResponseStatusException` | 统一异常响应格式 |

### 4.3 Lua 脚本加载与执行

`SlidingWindowUserIdLimiter` 核心实现：

```java
// 构造时加载 Lua 脚本
public SlidingWindowUserIdLimiter(RedisScript<Long> redisScript) {
    this.redisScript = redisScript;
}

// 执行限流判断
public boolean tryRecordDistinctUserIds(String key, List<String> userIds, 
        int maxDistinct, long windowMs) {
    List<String> args = new ArrayList<>();
    args.add(String.valueOf(System.currentTimeMillis()));  // now
    args.add(String.valueOf(windowMs));                    // windowMs
    args.add(String.valueOf(maxDistinct));                 // maxDistinct
    args.add(String.valueOf(TTL_SECONDS));                 // ttlSec
    args.addAll(userIds);                                     // UserId 列表
    
    Long result = stringRedisTemplate.execute(redisScript, 
        Collections.singletonList(key), args.toArray());
    
    return result != null && result == 1L;
}
```

---

## 五、配置说明

### 5.1 应用配置 (`application.yaml`)

```yaml
server:
  port: 10001

# 滑动窗口 UserId 校验配置
sw:
  user-id-guard:
    enabled: true                    # 是否启用过滤器
    uris:                            # Ant 风格 URI 匹配列表
      - /poc/test/**                 # 命中该模式的请求会被拦截校验

spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: ${REDIS_PASSWORD}
```

### 5.2 配置属性详解

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `sw.user-id-guard.enabled` | Boolean | true | 过滤器开关 |
| `sw.user-id-guard.uris` | List | [] | 需要拦截的 URI 模式列表 |

### 5.3 客户端配置（Redis 存储）

客户端限流参数存储在 Redis 中，格式为 JSON：

```json
{
    "clientId": "client001",
    "maxUserIdCount": 10,        // 窗口内最大 UserId 数量
    "timeWindow": 60          // 时间窗口大小（秒）
}
```

**缓存 Key 格式**：`config:clientId:{clientId}`

---

## 六、请求与响应

### 6.1 请求格式

| 参数位置 | 参数名 | 类型 | 必填 | 说明 |
|----------|--------|------|------|------|
| Header | `clientId` | String | 是 | 客户端唯一标识 |
| Header | `userId` | String | 是 | 用户标识 |

### 6.2 响应格式

**成功响应**（HTTP 200）：
```json
{
    "message": "success"
}
```

**失败响应**（HTTP 429）：
```json
{
    "message": "滑动窗口内该 client 操作的 UserId 数量超过上限"
}
```

---

## 七、设计亮点与技术优势

### 7.1 原子性保障

使用 Lua 脚本将多个 Redis 操作封装为原子操作，避免并发场景下的数据不一致问题。

### 7.2 动态窗口配置

支持通过请求参数动态调整窗口大小和限流阈值，无需重启服务。

### 7.3 高效过期清理

利用 Redis ZSET 的 `ZREMRANGEBYSCORE` 命令高效清理过期记录，避免数据堆积。

### 7.4 多层防护机制

| 防护层 | 实现 |
|--------|------|
| 参数校验 | 业务层参数合法性检查 |
| 上限约束 | `MAX_DISTINCT_USER_IDS_CAP` 服务端兜底 |
| TTL 自动回收 | Redis Key 自动过期 |

---

## 八、扩展建议

### 8.1 监控指标增强

建议增加以下监控指标：
- 限流拒绝次数
- 各客户端 UserId 操作频率
- Redis Key 数量统计

### 8.2 动态配置热更新

结合配置中心（如 Nacos、Apollo）实现限流参数的动态调整，无需修改代码。

### 8.3 分布式部署考量

在分布式场景下，需确保所有节点连接同一 Redis 实例，保证限流数据的一致性。

---

## 九、总结

本模块通过**动态时间滑动窗口算法**结合**Redis ZSET 数据结构**，实现了高效、可靠的 UserId 操作限流功能。核心优势包括：

1. **高性能**：基于 Redis 内存操作，响应延迟低
2. **高并发安全**：Lua 脚本保证原子性
3. **灵活性**：支持动态配置窗口大小和限流阈值
4. **可扩展性**：模块化设计，易于扩展和维护
