package com.yz.mall.auth.service;

import com.yz.mall.base.enums.MenuTypeEnum;
import com.yz.mall.redis.RedisCacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 权限缓存操作
 *
 * @author yunze
 * @since 2025/7/25 08:49
 */
@Slf4j
@Service
public class AuthOperateCacheService {

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    public AuthOperateCacheService(RedisTemplate<String, Object> defaultRedisTemplate) {
        this.defaultRedisTemplate = defaultRedisTemplate;
    }

    /**
     * 更新缓存
     *
     * @param type        权限类型
     * @param permissions Map<角色Id, List<权限>>
     */
    public void updateCache(MenuTypeEnum type, Map<String, List<String>> permissions) {
        // 执行 lua 脚本
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        // 指定 lua 脚本
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/RefreshPermission.lua")));
        // 指定返回类型
        redisScript.setResultType(Boolean.class);
        log.info("roleId信息：{}", permissions.keySet());
        for (String roleId : permissions.keySet()) {
            log.info("roleId permissions:{}", permissions.get(roleId));
            if (!permissions.containsKey(roleId)) {
                continue;
            }
            // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
            String[] array = permissions.get(roleId).toArray(new String[0]);
            defaultRedisTemplate.execute(redisScript, Collections.singletonList(RedisCacheKey.permission(type.name(), roleId)), array);
        }
    }
}
