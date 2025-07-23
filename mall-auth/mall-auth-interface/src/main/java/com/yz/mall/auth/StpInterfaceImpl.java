package com.yz.mall.auth;

import cn.dev33.satoken.stp.StpInterface;
import com.yz.mall.redis.RedisCacheKey;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义权限加载接口实现类
 *
 * @author yunze
 * @date 2024/11/15 16:09
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private RedisTemplate<String, Object> defaultRedisTemplate;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 返回此 loginId 拥有的权限列表
        List<String> permissionList = new ArrayList<>();

        List<Object> roles = defaultRedisTemplate.boundListOps(RedisCacheKey.permissionRole(loginId.toString())).range(0, -1);
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        // 用户拥有的角色
        List<String> roleIds = roles.stream().map(String::valueOf).toList();
        roleIds.forEach(roleId -> {
            List<Object> apiPermissions = defaultRedisTemplate.boundListOps(RedisCacheKey.permission("API", roleId)).range(0, -1);
            if (!CollectionUtils.isEmpty(apiPermissions)) {
                permissionList.addAll(apiPermissions.stream().map(String::valueOf).toList());
            }
        });

        return permissionList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 返回此 loginId 拥有的角色列表
        List<Object> roles = defaultRedisTemplate.boundListOps(RedisCacheKey.permissionRole(loginId.toString())).range(0, -1);
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return roles.stream().map(String::valueOf).collect(Collectors.toList());
    }
}
