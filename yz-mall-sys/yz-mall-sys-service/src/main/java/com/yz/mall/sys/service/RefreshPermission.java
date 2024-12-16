package com.yz.mall.sys.service;

import com.yz.mall.sys.enums.MenuTypeEnum;
import com.yz.redisson.RedisUtils;
import com.yz.tools.RedisCacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限信息刷新（按钮权限和接口权限）
 * @author yunze
 * @date 2024/12/15 星期日 11:07
 */
@Slf4j
@Component
public class RefreshPermission {

    private final RedisUtils redisUtils;

    private final RedisTemplate<String, Object> redisTemplate;

    private final PermissionService permissionService;

    public RefreshPermission(RedisUtils redisUtils
            , RedisTemplate<String, Object> redisTemplate
            , PermissionService permissionService) {
        this.redisUtils = redisUtils;
        this.redisTemplate = redisTemplate;
        this.permissionService = permissionService;
    }

    /**
     * 刷新按钮权限缓存信息
     */
    public void refreshButtonPermissionCache() {
        refreshPermissionCache(MenuTypeEnum.BUTTON);

    }

    /**
     * 刷新接口权限缓存信息
     */
    public void refreshApiPermissionCache() {
        refreshPermissionCache(MenuTypeEnum.API);
    }

    private void refreshPermissionCache(MenuTypeEnum type) {
        log.info("refresh permission cache");
        String cacheKey = RedisCacheKey.permission(type.name(), "*");
        List<String> keys = redisUtils.getKeysByPattern(cacheKey);
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

        List<String> roleIds = new ArrayList<>();
        for (String key : keys) {
            String[] split = key.split(":");
            roleIds.add(split[split.length - 1]);
        }

        Map<String, List<String>> permissions = permissionService.getPermissionsByRoleIds(type, roleIds.stream().map(Long::parseLong).collect(Collectors.toList()));
        // 执行 lua 脚本
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        // 指定 lua 脚本
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/RefreshPermission.lua")));
        // 指定返回类型
        redisScript.setResultType(Boolean.class);
        roleIds.forEach(roleId -> {
            // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
            String[] array = permissions.get(roleId).toArray(new String[0]);
            redisTemplate.execute(redisScript, Collections.singletonList(RedisCacheKey.permission(MenuTypeEnum.API.name(), roleId)), array);
        });
    }
}
