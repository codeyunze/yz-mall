-- KEYS[1]: 某个 client 对应的 ZSET；member=UserId，score=该 UserId 最近一次出现在请求中的时间（ms）
-- ARGV[1]: 当前时间 now（ms）
-- ARGV[2]: 滑动窗口长度 windowMs（ms）
-- ARGV[3]: 窗口内允许的最大不重复 UserId 种类数 maxDistinct
-- ARGV[4]: 整个 ZSET key 的 TTL（秒），用于 Redis 自动回收长期不访问的 key
-- ARGV[5..]: 本次请求携带的 UserId 列表（Java 侧已去空、去重；脚本内再防 ARGV 重复）
--
-- 返回 1：允许本次请求，并已 ZADD 刷新各 UserId 的 score；返回 0：如果操作的 UserId 数量超过了限制，需要拒绝本次请求且不写入 ZSET

local key = KEYS[1]
local now = tonumber(ARGV[1])
local windowMs = tonumber(ARGV[2])
local maxDistinct = tonumber(ARGV[3])
local ttlSec = tonumber(ARGV[4])

-- 计算滑动窗口左边界（当前时间 - 窗口时间长度 = 窗口的左边界时间）（所有小于这个窗口左边界时间的 UserId 都需要被删除）
local cut = now - windowMs
-- 按 score 范围删除已滑出滑动窗口的 member（清理过期的 UserId）
redis.call('ZREMRANGEBYSCORE', key, '-inf', cut - 1)

-- 裁剪后仍留在 ZSET 中的 UserId 数量（当前窗口内仍有效的不同的 UserId 总数）
local current = redis.call('ZCARD', key)

-- 统计：本次请求里，有多少个 UserId 在 ZSET 中不存在（需要插入到 zset 中的 UserId 数量）
local seen = {}
local newCount = 0
for i = 5, #ARGV do
  local userId = ARGV[i]
  if userId ~= nil and userId ~= '' and seen[userId] == nil then
    seen[userId] = true
    -- Redis 对不存在 member 的 ZSCORE 在 Lua 里为 false
    if redis.call('ZSCORE', key, userId) == false then
      newCount = newCount + 1
    end
  end
end

-- 如果本次新增 UserId 加上已经存在的 UserId，超过了最大限制，则拒绝本次请求
if current + newCount > maxDistinct then
  return 0
end

-- 对已存在于集合的 UserId，ZADD 同 member 会更新 score，相当于「续期」仍在窗口内
for userId, _ in pairs(seen) do
  redis.call('ZADD', key, now, userId)
end

redis.call('EXPIRE', key, ttlSec)
return 1
