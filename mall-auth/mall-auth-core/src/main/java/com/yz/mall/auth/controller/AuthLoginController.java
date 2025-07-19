package com.yz.mall.auth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.yz.mall.auth.dto.*;
import com.yz.mall.auth.service.AuthSysRoleRelationMenuService;
import com.yz.mall.auth.service.AuthSysUserService;
import com.yz.mall.auth.vo.AuthUserIntegratedInfoDto;
import com.yz.mall.auth.vo.AuthUserInfoVo;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.enums.MenuTypeEnum;
import com.yz.mall.redis.RedisCacheKey;
import com.yz.mall.web.annotation.RepeatSubmit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 身份认证接口
 *
 * @author yunze
 * @date 2024/7/30 23:16
 */
@Slf4j
@RestController
@RequestMapping
public class AuthLoginController extends ApiController {

    private final AuthSysUserService authSysUserService;

    private final AuthSysRoleRelationMenuService authSysRoleRelationMenuService;

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    public AuthLoginController(RedisTemplate<String, Object> defaultRedisTemplate
            , AuthSysUserService authSysUserService
            , AuthSysRoleRelationMenuService authSysRoleRelationMenuService) {
        this.defaultRedisTemplate = defaultRedisTemplate;
        this.authSysUserService = authSysUserService;
        this.authSysRoleRelationMenuService = authSysRoleRelationMenuService;
    }

    /**
     * 登录接口
     */
    @SaIgnore
    @PostMapping("login")
    public Result<AuthUserIntegratedInfoDto> login(@RequestBody @Valid AuthLoginDto loginDto) {
        AuthUserBaseInfoDto loginInfo = authSysUserService.checkLogin(new AuthSysUserCheckLoginDto(loginDto.getAccount(), loginDto.getPassword()));
        if (loginInfo == null) {
            return new Result<>(CodeEnum.AUTHENTICATION_ERROR.get(), null, "登录失败");
        }

        // 登录
        StpUtil.login(loginInfo.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        List<String> roles = getRoleByUserId(loginInfo.getId());

        AuthUserIntegratedInfoDto vo = new AuthUserIntegratedInfoDto();
        BeanUtils.copyProperties(loginInfo, vo);
        vo.setUserId(loginInfo.getId());
        vo.setAccessToken(tokenInfo.tokenValue);
        vo.setExpires(LocalDateTimeUtil.offset(LocalDateTime.now(), tokenInfo.tokenTimeout, ChronoUnit.SECONDS));
        // 刷新令牌有效期1天
        vo.setRefreshToken(SaTempUtil.createToken(loginInfo.getId(), 86400));
        vo.setRoles(roles);

        // 获取指定角色所拥有的按钮权限，同时查询接口权限，并缓存
        getPermissionByRoleIds(roles);
        vo.setPermissions(getPermissionByRoleIds(roles));
        vo.setAvatar(loginInfo.getAvatar());

        BoundHashOperations<String, Object, Object> operations = defaultRedisTemplate.boundHashOps(RedisCacheKey.loginInfo(vo.getUserId()));
        operations.put("refreshToken", vo.getRefreshToken());
        operations.put("userId", vo.getUserId());
        operations.put("username", vo.getUsername());
        operations.put("nickname", vo.getNickname());
        operations.put("accessToken", vo.getAccessToken());
        operations.expire(86400, TimeUnit.SECONDS);
        return success(vo);
    }


    /**
     * 获取指定用户拥有的角色信息，并缓存到redis
     *
     * @param userId 指定用户Id
     * @return 用户拥有的角色
     */
    private List<String> getRoleByUserId(Long userId) {
        // 查询用户角色
        List<Long> roles = authSysUserService.getUserRoles(userId);
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }

        return roles.stream().map(String::valueOf).collect(Collectors.toList());
    }

    /**
     * 获取指定角色所拥有的按钮权限，同时查询接口权限，并缓存
     *
     * @param roleIds 角色Id列表
     * @return 按钮资源权限
     */
    private List<String> getPermissionByRoleIds(List<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        // 所拥有的所有按钮权限
        List<String> permissions = new ArrayList<>();

        AuthRolePermissionQueryDto queryDto = new AuthRolePermissionQueryDto();
        queryDto.setMenuType(MenuTypeEnum.BUTTON);
        queryDto.setRoleIds(roleIds.stream().map(Long::parseLong).collect(Collectors.toList()));
        Map<String, List<String>> permissionsBtnByRoleIds = authSysRoleRelationMenuService.getPermissionsByRoleIds(queryDto);

        queryDto.setMenuType(MenuTypeEnum.API);
        authSysRoleRelationMenuService.getPermissionsByRoleIds(queryDto);

        if (!CollectionUtils.isEmpty(permissionsBtnByRoleIds)) {
            permissionsBtnByRoleIds.forEach((key, value) -> permissions.addAll(value));
        }

        if (CollectionUtils.isEmpty(permissions)) {
            return Collections.emptyList();
        }
        return permissions.stream().distinct().collect(Collectors.toList());
    }


    /**
     * 更新访问令牌
     *
     * @param refreshTokenDto 刷新令牌
     * @return 新的访问令牌
     */
    @SaIgnore
    @PostMapping("/refreshToken")
    public Result<AuthUserIntegratedInfoDto> refreshToken(@RequestBody @Valid RefreshTokenDto refreshTokenDto) {
        // 1、验证
        Object userId = SaTempUtil.parseToken(refreshTokenDto.getRefreshToken());
        if (userId == null) {
            return new Result<>(CodeEnum.ERROR_TOKEN_ILLEGAL.get(), null, "无效的刷新令牌");
        }

        // 2、为其生成新的短 token
        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        // 获取并缓存角色信息
        getRoleByUserId(Long.parseLong(userId.toString()));

        // 3、返回信息
        AuthUserIntegratedInfoDto vo = new AuthUserIntegratedInfoDto();
        vo.setAccessToken(tokenInfo.getTokenValue());
        vo.setRefreshToken(SaTempUtil.createToken(userId, 86400));
        vo.setExpires(LocalDateTimeUtil.offset(LocalDateTime.now(), tokenInfo.tokenTimeout, ChronoUnit.SECONDS));
        vo.setUserId(Long.parseLong(userId.toString()));
        // 清理旧的临时token
        SaTempUtil.deleteToken(refreshTokenDto.getRefreshToken());

        BoundHashOperations<String, Object, Object> operations = defaultRedisTemplate.boundHashOps(RedisCacheKey.loginInfo(vo.getUserId()));
        operations.put("refreshToken", vo.getRefreshToken());
        operations.expire(86400, TimeUnit.SECONDS);
        return new Result<>(CodeEnum.SUCCESS.get(), vo, "访问令牌更新成功");
    }


    /**
     * 登出系统
     */
    @SaIgnore
    @RequestMapping("logout")
    public Result<?> logout() {
        if (!StpUtil.isLogin()) {
            return new Result<>(CodeEnum.SUCCESS.get(), null, "系统登出成功");
        }
        // 清理刷新token
        Long userId = StpUtil.getLoginIdAsLong();
        if (!StringUtils.hasText(StpUtil.getLoginIdAsString())) {
            return new Result<>(CodeEnum.SUCCESS.get(), null, "系统登出成功");
        }
        // 清理角色缓存信息
        defaultRedisTemplate.delete(RedisCacheKey.permissionRole(StpUtil.getTokenValue()));

        // 清理用户信息
        Object refreshToken = defaultRedisTemplate.boundHashOps(RedisCacheKey.loginInfo(userId)).get("refreshToken");
        if (!ObjectUtils.isEmpty(refreshToken)) {
            defaultRedisTemplate.delete(RedisCacheKey.loginInfo(userId));
            // 清理刷新token
            SaTempUtil.deleteToken(refreshToken.toString());
        }

        // 清理登录token
        StpUtil.logout();
        return new Result<>(CodeEnum.SUCCESS.get(), null, "系统登出成功");
    }


    /**
     * 查询登录状态
     *
     * @return true: 已登录    false: 未登录
     */
    @RequestMapping("isLogin")
    public Result<Boolean> isLogin() {
        // 获取当前会话的 token 值
        String tokenValue = StpUtil.getTokenValue();
        log.info("tokenValue:{}", tokenValue);

        // 获取当前`StpLogic`的 token 名称
        String tokenName = StpUtil.getTokenName();
        log.info(tokenName);

        // 获取当前会话剩余有效期（单位：s，返回-1代表永久有效）
        long tokenTimeout = StpUtil.getTokenTimeout();
        log.info("token timeout: {}", tokenTimeout);

        // 获取当前会话的 token 信息参数
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        log.info("tokenInfo:{}", tokenInfo);

        // 获取指定 token 对应的账号id，如果未登录，则返回 null
        Object loginIdByToken = StpUtil.getLoginIdByToken(tokenValue);
        log.info("loginIdByToken:{}", loginIdByToken);

        return success(StpUtil.isLogin());
    }

    @RequestMapping("test")
    public Result<Boolean> test() {
        String password = "123456";

        String salt = BCrypt.gensalt(12);
        log.info("salt:{}", salt);

        String pw_hash1 = BCrypt.hashpw(password, salt);
        log.info("pw_hash1:{}", pw_hash1);

        // 使用checkpw方法检查被加密的字符串是否与原始字符串匹配：
        boolean checkpwed = BCrypt.checkpw(password, pw_hash1);
        log.info("checkpwed:{}", checkpwed);

        return success(Boolean.TRUE);
    }

    /**
     * 注册用户
     *
     * @param dto 注册用户信息
     * @return 注册用户Id
     */
    @RepeatSubmit
    @SaIgnore
    @PostMapping("register")
    public Result<Long> register(@Valid @RequestBody RegisterDto dto) {
        // InternalSysUserAddDto userAddDto = new InternalSysUserAddDto();
        // BeanUtils.copyProperties(registerDto, userAddDto);
        return success(authSysUserService.add(dto));
    }

    /**
     * 获取请求者的登录信息
     */
    @SaCheckLogin
    @SaIgnore
    @GetMapping("getUserInfo")
    public Result<AuthUserIntegratedInfoDto> getUserInfo() {
        AuthUserInfoVo loginInfo = authSysUserService.getUserInfoById(StpUtil.getLoginIdAsLong());
        if (loginInfo == null) {
            // 数据库里不存在该用户信息，清理该token的信息
            StpUtil.logout();
            return new Result<>(CodeEnum.ERROR_TOKEN_ILLEGAL.get(), null, CodeEnum.ERROR_TOKEN_ILLEGAL.getMsg());
        }

        List<String> roles = getRoleByUserId(StpUtil.getLoginIdAsLong());
        AuthUserIntegratedInfoDto vo = new AuthUserIntegratedInfoDto();
        BeanUtils.copyProperties(loginInfo, vo);
        vo.setUserId(loginInfo.getId());

        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        vo.setAccessToken(tokenInfo.tokenValue);
        vo.setExpires(LocalDateTimeUtil.offset(LocalDateTime.now(), tokenInfo.tokenTimeout, ChronoUnit.SECONDS));
        // 获取刷新令牌
        vo.setRefreshToken((String) defaultRedisTemplate.boundHashOps(RedisCacheKey.loginInfo(vo.getUserId())).get("refreshToken"));
        vo.setRoles(roles);
        vo.setPermissions(getPermissionByRoleIds(roles));
        vo.setAvatar(loginInfo.getAvatar());
        return success(vo);
    }

}
