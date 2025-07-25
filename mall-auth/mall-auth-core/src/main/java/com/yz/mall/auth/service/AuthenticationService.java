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

    private final AuthSysUserService authSysUserService;

    private final AuthSysRoleRelationMenuService roleRelationMenuService;

    private final AuthOperateCacheService authOperateCacheService;

    public AuthenticationService(RedisTemplate<String, Object> defaultRedisTemplate
            , AuthSysUserService authSysUserService
            , AuthSysRoleRelationMenuService roleRelationMenuService
            , AuthOperateCacheService authOperateCacheService) {
        this.defaultRedisTemplate = defaultRedisTemplate;
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
     * 获取指定角色所拥有的按钮权限，同时查询接口权限，并缓存
     *
     * @param roleIds 角色Id列表
     * @return 按钮资源权限
     */
    private List<String> getPermissionByRoleIds(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }

        // 查询并缓存指定角色的按钮权限
        Map<String, List<String>> permissionsBtnByRoleIds = roleRelationMenuService.getPermissionsByRoleIds(new AuthRolePermissionQueryDto(MenuTypeEnum.BUTTON, roleIds));
        authOperateCacheService.updateCache(MenuTypeEnum.BUTTON, permissionsBtnByRoleIds);

        // 查询并缓存指定角色的API权限
        Map<String, List<String>> permissionsApiByRoleIds = roleRelationMenuService.getPermissionsByRoleIds(new AuthRolePermissionQueryDto(MenuTypeEnum.API, roleIds));
        authOperateCacheService.updateCache(MenuTypeEnum.API, permissionsApiByRoleIds);

        // 所拥有的所有按钮权限
        return permissionsBtnByRoleIds.values().stream().flatMap(Collection::stream).distinct().toList();
    }
}
