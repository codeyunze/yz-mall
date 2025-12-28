package com.yz.mall.auth.controller;

import cn.hutool.core.util.IdUtil;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.yz.mall.auth.dto.*;
import com.yz.mall.auth.service.AuthSysUserService;
import com.yz.mall.auth.service.AuthenticationService;
import com.yz.mall.auth.utils.LoginLogUtil;
import com.yz.mall.auth.vo.AuthUserInfoVo;
import com.yz.mall.auth.vo.AuthUserIntegratedInfoDto;
import com.yz.mall.base.ApiController;
import com.yz.mall.base.Result;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.redis.RedisCacheKey;
import com.yz.mall.sys.entity.SysLoginLog;
import com.yz.mall.sys.mapper.SysLoginLogMapper;
import com.yz.mall.web.annotation.RepeatSubmit;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 身份认证接口
 *
 * @author yunze
 * @date 2024/7/30 23:16
 */
@Slf4j
@RestController
@RequestMapping("/authentication")
public class AuthenticationController extends ApiController {

    private final AuthSysUserService authSysUserService;

    private final AuthenticationService authenticationService;

    private final RedisTemplate<String, Object> defaultRedisTemplate;

    private final SysLoginLogMapper sysLoginLogMapper;

    public AuthenticationController(RedisTemplate<String, Object> defaultRedisTemplate
            , AuthSysUserService authSysUserService
            , AuthenticationService authenticationService
            , SysLoginLogMapper sysLoginLogMapper) {
        this.defaultRedisTemplate = defaultRedisTemplate;
        this.authSysUserService = authSysUserService;
        this.authenticationService = authenticationService;
        this.sysLoginLogMapper = sysLoginLogMapper;
    }

    /**
     * 登录接口
     */
    @SaIgnore
    @PostMapping("login")
    public Result<AuthUserIntegratedInfoDto> login(@RequestBody @Valid AuthLoginDto loginDto, HttpServletRequest request) {
        String username = loginDto.getAccount();
        String loginIp = null;
        String loginLocation = null;
        String os = null;
        String browser = null;
        Integer loginType = 1; // 默认账号登录

        try {
            // 获取请求信息
            if (request == null) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    request = attributes.getRequest();
                }
            }
            if (request != null) {
                loginIp = LoginLogUtil.getClientIp(request);
                loginLocation = LoginLogUtil.getLoginLocation(loginIp);
                os = LoginLogUtil.getOs(request);
                browser = LoginLogUtil.getBrowser(request);
            }
        } catch (Exception e) {
            log.error("获取登录信息失败", e);
        }

        try {
            AuthUserBaseInfoDto loginInfo = authSysUserService.checkLogin(new AuthSysUserCheckLoginDto(loginDto.getAccount(), loginDto.getPassword()));
            if (loginInfo == null) {
                // 记录登录失败日志
                recordLoginLog(0L, username, loginIp, loginLocation, os, browser, 0, loginType);
                return new Result<>(CodeEnum.AUTHENTICATION_ERROR.get(), null, "登录失败");
            }

            // 登录
            StpUtil.login(loginInfo.getId());

            // 登录成功，获取用户信息
            AuthUserIntegratedInfoDto userInfo = authenticationService.getUserInfo(loginInfo.getId());
            userInfo.setAvatar(loginInfo.getAvatar());
            userInfo.setUsername(loginInfo.getUsername());
            userInfo.setNickname(loginInfo.getUsername());

            // 记录登录成功日志
            recordLoginLog(userInfo.getUserId(), username, loginIp, loginLocation, os, browser, 1, loginType);

            return success(userInfo);
        } catch (Exception e) {
            // 记录登录失败日志
            recordLoginLog(0L, username, loginIp, loginLocation, os, browser, 0, loginType);
            throw e;
        }
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(Long userId, String username, String loginIp, String loginLocation, String os, String browser, Integer status, Integer loginType) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setId(IdUtil.getSnowflakeNextId());
            loginLog.setUserId(userId);
            loginLog.setUsername(username);
            loginLog.setLoginIp(loginIp != null ? loginIp : "未知");
            loginLog.setLoginLocation(loginLocation != null ? loginLocation : "未知");
            loginLog.setOs(os != null ? os : "未知");
            loginLog.setBrowser(browser != null ? browser : "未知");
            loginLog.setStatus(status);
            loginLog.setLoginType(loginType);
            loginLog.setLoginTime(LocalDateTime.now());
            loginLog.setCreateTime(LocalDateTime.now());
            loginLog.setUpdateTime(LocalDateTime.now());
            sysLoginLogMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("记录登录日志失败", e);
        }
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

        // 清理旧的临时token
        SaTempUtil.deleteToken(refreshTokenDto.getRefreshToken());
        AuthUserIntegratedInfoDto vo = authenticationService.getUserInfo(Long.parseLong(userId.toString()));
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

        AuthUserIntegratedInfoDto vo = authenticationService.getUserInfo(loginInfo.getId());
        vo.setAvatar(loginInfo.getAvatar());
        vo.setUsername(loginInfo.getUsername());
        vo.setNickname(loginInfo.getUsername());
        return success(vo);
    }

    /**
     * 验证用户权限
     *
     * @param dto 验证权限信息
     * @return true: 验证通过    false: 验证失败
     */
    @PostMapping("checkPermission")
    public Result<Boolean> checkPermission(@RequestBody @Valid AuthCheckPermissionDto dto) {
        return success(authenticationService.checkPermission(dto));
    }
}
