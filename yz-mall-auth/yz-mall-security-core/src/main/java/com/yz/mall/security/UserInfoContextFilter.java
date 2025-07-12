package com.yz.mall.security;

import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.sys.service.InternalSysUserRelationOrgService;
import com.yz.mall.sys.vo.InternalSysUserRelationOrgVo;
import com.yz.mall.web.common.RedisCacheKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户信息赋值过滤器
 *
 * @author yunze
 * @date 2024/8/5 23:25
 */
@Slf4j
@Order(98)
@Component
public class UserInfoContextFilter implements Filter {

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    private final InternalSysUserRelationOrgService internalSysUserRelationOrgService;

    public UserInfoContextFilter(RedisTemplate<String, Object> defaultRedisTemplate, InternalSysUserRelationOrgService internalSysUserRelationOrgService) {
        this.defaultRedisTemplate = defaultRedisTemplate;
        this.internalSysUserRelationOrgService = internalSysUserRelationOrgService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.info("98-用户信息赋值过滤器");
        if (UserInfoContextHolder.get() == null) {

        }
        Long userId = StpUtil.getLoginIdAsLong();
        BoundHashOperations<String, Object, Object> operations = defaultRedisTemplate.boundHashOps(RedisCacheKey.loginInfo(userId));
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(Long.valueOf(Objects.requireNonNull(operations.get("userId")).toString()));
        userInfo.setUsername(String.valueOf(operations.get("username")));
        userInfo.setNickname(String.valueOf(operations.get("nickname")));

        List<Object> roles = defaultRedisTemplate.boundListOps(RedisCacheKey.permissionRole(String.valueOf(userId))).range(0, -1);
        if (!CollectionUtils.isEmpty(roles)) {
            userInfo.setRoles(roles.stream().map(String::valueOf).collect(Collectors.toList()));
        }

        List<InternalSysUserRelationOrgVo> orgVos = internalSysUserRelationOrgService.getOrgByUserId(userId);
        if (!CollectionUtils.isEmpty(orgVos)) {
            userInfo.setOrganizations(orgVos.stream().map(t -> {
                OrgInfo org = new OrgInfo();
                BeanUtils.copyProperties(t, org);
                return org;
            }).collect(Collectors.toList()));
        }

        UserInfoContextHolder.set(userInfo);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        UserInfoContextHolder.clear();
        Filter.super.destroy();
    }
}
