package com.yz.mall.auth.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.yz.mall.auth.dto.AuthCheckPermissionDto;
import com.yz.mall.auth.dto.AuthRolePermissionQueryDto;
import com.yz.mall.auth.vo.AuthUserIntegratedInfoDto;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.enums.MenuTypeEnum;
import com.yz.mall.base.exception.AuthenticationException;
import com.yz.mall.redis.RedisCacheKey;
import com.yz.mall.redis.RedissonLockKey;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证接口
 *
 * @author yunze
 * @since 2025/7/23 18:20
 */
@Service
public class AuthenticationService {

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    private final Redisson redisson;

    private final AuthSysUserService authSysUserService;

    private final AuthSysRoleRelationMenuService roleRelationMenuService;

    private final AuthOperateCacheService authOperateCacheService;

    public AuthenticationService(RedisTemplate<String, Object> defaultRedisTemplate
            , Redisson redisson
            , AuthSysUserService authSysUserService
            , AuthSysRoleRelationMenuService roleRelationMenuService
            , AuthOperateCacheService authOperateCacheService) {
        this.defaultRedisTemplate = defaultRedisTemplate;
        this.redisson = redisson;
        this.authSysUserService = authSysUserService;
        this.roleRelationMenuService = roleRelationMenuService;
        this.authOperateCacheService = authOperateCacheService;
    }

    /**
     * 获取登录用户信息
     *
     * @param userId 用户Id
     * @return 用户信息
     */
    public AuthUserIntegratedInfoDto getUserInfo(Long userId) {
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        List<Long> roles = authSysUserService.getUserRoles(userId);

        AuthUserIntegratedInfoDto vo = new AuthUserIntegratedInfoDto();
        // BeanUtils.copyProperties(loginInfo, vo);
        vo.setUserId(userId);
        vo.setAccessToken(tokenInfo.tokenValue);
        vo.setExpires(LocalDateTimeUtil.offset(LocalDateTime.now(), tokenInfo.tokenTimeout, ChronoUnit.SECONDS));
        // 刷新令牌有效期1天
        vo.setRefreshToken(SaTempUtil.createToken(userId, 86400));
        vo.setRoles(roles);

        // 获取指定角色所拥有的按钮权限，同时查询接口权限，并缓存
        vo.setBtnPermissions(getPermissionByRoleIds(roles));
        // vo.setAvatar(loginInfo.getAvatar());

        BoundHashOperations<String, Object, Object> operations = defaultRedisTemplate.boundHashOps(RedisCacheKey.loginInfo(vo.getUserId()));
        operations.put("refreshToken", vo.getRefreshToken());
        operations.put("userId", vo.getUserId());
        operations.put("username", vo.getUsername());
        operations.put("nickname", vo.getNickname());
        operations.put("accessToken", vo.getAccessToken());
        operations.expire(86400, TimeUnit.SECONDS);
        return vo;
    }


    /**
     * 检查用户权限
     *
     * @param dto 检查权限参数
     * @return 检查结果
     */
    public Boolean checkPermission(AuthCheckPermissionDto dto) {
        BoundListOperations<String, Object> userRoleOpt = defaultRedisTemplate.boundListOps(RedisCacheKey.permissionRole(StpUtil.getLoginIdAsString()));
        List<Object> userRoles = userRoleOpt.range(0, -1);
        if (CollectionUtils.isEmpty(userRoles)) {
            throw new AuthenticationException(CodeEnum.AUTHENTICATION_ERROR.getMsg());
        }

        for (Object userRole : userRoles) {
            String roleId = userRole.toString();
            List<Object> userPermissions = defaultRedisTemplate.boundListOps(RedisCacheKey.permission(dto.getType().name(), roleId)).range(0, -1);
            assert userPermissions != null;
            Set<String> userPermissionSet = userPermissions.stream().map(String::valueOf).collect(Collectors.toSet());
            if (userPermissionSet.contains(dto.getPermission())) {
                return Boolean.TRUE;
            }
        }

        throw new AuthenticationException(CodeEnum.AUTHENTICATION_ERROR.getMsg());
    }


    /**
     * 获取并返回指定角色所拥有的按钮权限，同时仅查询接口权限。按钮权限和接口权限都缓存。
     *
     * @param roleIds 角色Id列表
     * @return 按钮资源权限
     */
    private List<String> getPermissionByRoleIds(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }

        // 是否需要去查库
        boolean needToQuery;
        // 缓存的按钮权限
        List<String> cachedButtonPermissions = new ArrayList<>();
        // 判断缓存是否存在所需角色的权限信息
        needToQuery = getCacheNeedToQuery(cachedButtonPermissions, roleIds);

        // 如果不需要查库，直接返回按钮的权限信息即可
        if (!needToQuery) {
            return cachedButtonPermissions;
        }

        RLock rLock = redisson.getLock(RedissonLockKey.keyRefreshPermission());
        rLock.lock(20, TimeUnit.SECONDS);
        try {
            // 双重检查锁，防止获取锁后，因为前一个线程已经获取了锁，并已经更新了缓存，然后导致当前线程在之前获取的缓存数据不完整
            if (!getCacheNeedToQuery(cachedButtonPermissions, roleIds)) {
                return cachedButtonPermissions;
            }

            // 查询并缓存指定角色的按钮权限
            Map<String, List<String>> permissionsBtnByRoleIds = roleRelationMenuService.getPermissionsByRoleIds(new AuthRolePermissionQueryDto(MenuTypeEnum.BUTTON, roleIds));
            authOperateCacheService.updateCache(MenuTypeEnum.BUTTON, permissionsBtnByRoleIds);

            // 查询并缓存指定角色的API权限
            Map<String, List<String>> permissionsApiByRoleIds = roleRelationMenuService.getPermissionsByRoleIds(new AuthRolePermissionQueryDto(MenuTypeEnum.API, roleIds));
            authOperateCacheService.updateCache(MenuTypeEnum.API, permissionsApiByRoleIds);

            // 所拥有的所有按钮权限
            return permissionsBtnByRoleIds.values().stream().flatMap(Collection::stream).distinct().toList();
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 判断缓存是否存在所需角色的权限信息
     *
     * @param cachedButtonPermissions 缓存里的按钮权限信息
     * @param roleIds                 角色Id列表
     * @return 是否需要去查库
     */
    private boolean getCacheNeedToQuery(List<String> cachedButtonPermissions, List<Long> roleIds) {
        // 判断缓存是否存在所需角色的权限信息
        for (Long roleId : roleIds) {
            List<Object> buttonPermissions = defaultRedisTemplate.boundListOps(RedisCacheKey.permission(MenuTypeEnum.BUTTON.name(), roleId.toString())).range(0, -1);
            if (CollectionUtils.isEmpty(buttonPermissions)) {
                return true;
            } else {
                cachedButtonPermissions.addAll(buttonPermissions.stream().map(String::valueOf).toList());
            }
            List<Object> apiPermissions = defaultRedisTemplate.boundListOps(RedisCacheKey.permission(MenuTypeEnum.API.name(), roleId.toString())).range(0, -1);
            if (CollectionUtils.isEmpty(apiPermissions)) {
                return true;
            }
        }
        return false;
    }
}
