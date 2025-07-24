package com.yz.mall.sys.service;

import com.yz.mall.auth.dto.AuthRolePermissionQueryDto;
import com.yz.mall.auth.service.ExtendPermissionService;
import com.yz.mall.base.enums.MenuTypeEnum;
import com.yz.mall.redis.RedisCacheKey;
import com.yz.mall.redis.RedisUtils;
import jakarta.annotation.PostConstruct;
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

/**
 * 权限信息刷新（按钮权限和接口权限）
 *
 * @author yunze
 * @date 2024/12/15 星期日 11:07
 */
@Slf4j
@Component
public class RefreshPermission {

    private final RedisUtils redisUtils;

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    private final ExtendPermissionService extendPermissionService;

    private final SysRoleService roleService;

    public RefreshPermission(RedisUtils redisUtils
            , RedisTemplate<String, Object> defaultRedisTemplate
            , ExtendPermissionService extendPermissionService
            , SysRoleService roleService) {
        this.redisUtils = redisUtils;
        this.defaultRedisTemplate = defaultRedisTemplate;
        this.extendPermissionService = extendPermissionService;
        this.roleService = roleService;
    }

    @PostConstruct
    public void loadRolePermissionCache() {
        log.info("refresh all permission cache");
        // refreshPermissionCache(MenuTypeEnum.BUTTON);
        // refreshPermissionCache(MenuTypeEnum.API);
        List<Long> roleIds = roleService.getEffectiveRoleIds();
        if (CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        extendPermissionService.getPermissionsByRoleIds(new AuthRolePermissionQueryDto(MenuTypeEnum.BUTTON, roleIds));
    }

    /**
     * 刷新所有角色的按钮权限缓存信息
     */
    public void refreshButtonPermissionCache() {
        refreshPermissionCache(MenuTypeEnum.BUTTON);
    }

    /**
     * 刷新所有角色的接口权限缓存信息
     */
    public void refreshApiPermissionCache() {
        refreshPermissionCache(MenuTypeEnum.API);
    }


    /**
     * 刷新指定类型权限缓存信息
     *
     * @param type 权限类型
     */
    private void refreshPermissionCache(MenuTypeEnum type) {
        log.info("refresh permission cache");
        // 获取到所有在缓存里的指定类型（按钮或接口）的权限缓存的key
        String cacheKey = RedisCacheKey.permission(type.name(), "*");
        List<String> keys = redisUtils.getKeysByPattern(cacheKey);
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }

        // 解析权限缓存key，获取到角色id
        List<Long> roleIds = new ArrayList<>();
        for (String key : keys) {
            String[] split = key.split(":");
            roleIds.add(Long.parseLong(split[split.length - 1]));
        }

        // 获取指定角色，指定菜单类型所拥有的权限
        Map<String, List<String>> permissions = extendPermissionService.getPermissionsByRoleIds(new AuthRolePermissionQueryDto(type, roleIds));
        // 更新缓存
        updateCache(type, permissions);
    }

    /**
     * 更新缓存
     *
     * @param type        权限类型
     * @param permissions Map<角色Id, List<权限>>
     */
    private void updateCache(MenuTypeEnum type, Map<String, List<String>> permissions) {
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
