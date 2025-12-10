-- 保存权限映射到Redis的Lua脚本
-- KEYS[1]: 服务名的匹配模式，例如 "permission:api-mapping:mall-sys:*"
-- ARGV[1..N]: 交替的 key 和 value，格式为 key1, value1, key2, value2, ...
-- 例如：ARGV = {"permission:api-mapping:mall-sys:/sys/dictionary/add", "api:system:dictionary:update", ...}

local pattern = KEYS[1]
local deletedCount = 0

-- 使用 SCAN 删除旧映射（避免 KEYS 命令阻塞）
local cursor = "0"
repeat
    local result = redis.call("SCAN", cursor, "MATCH", pattern, "COUNT", 100)
    cursor = result[1]
    local keys = result[2]
    
    if #keys > 0 then
        redis.call("DEL", unpack(keys))
        deletedCount = deletedCount + #keys
    end
until cursor == "0"

-- 保存新映射
local savedCount = 0
for i = 1, #ARGV, 2 do
    if i + 1 <= #ARGV then
        local key = ARGV[i]
        local value = ARGV[i + 1]
        redis.call("SET", key, value)
        savedCount = savedCount + 1
    end
end

return {deletedCount, savedCount}

