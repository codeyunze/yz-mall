package com.yz.mall.security.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.yz.mall.security.dto.AuthLoginDto;
import com.yz.mall.security.dto.RefreshTokenDto;
import com.yz.mall.security.vo.UserLoginInfoVo;
import com.yz.mall.sys.dto.InternalLoginInfoDto;
import com.yz.mall.sys.dto.InternalSysUserCheckLoginDto;
import com.yz.mall.sys.service.InternalSysUserService;
import com.yz.tools.ApiController;
import com.yz.tools.RedisCacheKey;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 身份认证接口
 *
 * @author yunze
 * @date 2024/7/30 23:16
 */
@Slf4j
@RestController
@RequestMapping
public class LoginController extends ApiController {

    @Resource
    private InternalSysUserService internalSysUserService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 登录接口
     */
    @PostMapping("login")
    public Result<UserLoginInfoVo> login(@RequestBody @Valid AuthLoginDto loginDto) {
        InternalLoginInfoDto loginInfo = internalSysUserService.checkLogin(new InternalSysUserCheckLoginDto(loginDto.getUsername(), loginDto.getPassword()));
        if (loginInfo == null) {
            return new Result<>(CodeEnum.AUTHENTICATION_ERROR.get(), null, "登录失败");
        }

        // 登录
        StpUtil.login(loginInfo.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        List<Long> roles = getRoleByUserId(loginInfo.getId());

        UserLoginInfoVo vo = new UserLoginInfoVo();
        BeanUtils.copyProperties(loginInfo, vo);
        vo.setUserId(loginInfo.getId());
        vo.setAccessToken(tokenInfo.tokenValue);
        vo.setExpires(LocalDateTimeUtil.offset(LocalDateTime.now(), tokenInfo.tokenTimeout, ChronoUnit.SECONDS));
        // 刷新令牌有效期1天
        vo.setRefreshToken(SaTempUtil.createToken(loginInfo.getId(), 86400));
        vo.setRoles(roles);
        vo.setAvatar(loginInfo.getAvatar());
        return success(vo);
    }


    /**
     * 获取指定用户拥有的角色信息，并缓存到redis
     *
     * @param userId 指定用户Id
     * @return 用户拥有的角色
     */
    private List<Long> getRoleByUserId(String userId) {
        List<Long> roles = internalSysUserService.getUserRoles(Long.parseLong(userId));
        if (!CollectionUtils.isEmpty(roles)) {
            // 缓存用户所拥有的角色信息
            roles.forEach(role -> redisTemplate.opsForList().rightPush(RedisCacheKey.permissionRole(userId), role));
            redisTemplate.expire(RedisCacheKey.permissionRole(userId), 86400, TimeUnit.SECONDS);
        }
        return roles;
    }


    /**
     * 更新访问令牌
     *
     * @param refreshTokenDto 刷新令牌
     * @return 新的访问令牌
     */
    @PostMapping("/refreshToken")
    public Result<UserLoginInfoVo> refreshToken(@RequestBody @Valid RefreshTokenDto refreshTokenDto) {
        // 1、验证
        Object userId = SaTempUtil.parseToken(refreshTokenDto.getRefreshToken());
        if (userId == null) {
            return new Result<>(CodeEnum.AUTHENTICATION_ERROR.get(), null, "无效的刷新令牌");
        }

        // 2、为其生成新的短 token
        StpUtil.login(userId);
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        // 获取并缓存角色信息
        getRoleByUserId(userId.toString());

        // 3、返回信息
        UserLoginInfoVo vo = new UserLoginInfoVo();
        vo.setAccessToken(tokenInfo.getTokenValue());
        vo.setRefreshToken(SaTempUtil.createToken(userId, 86400));
        vo.setExpires(LocalDateTimeUtil.offset(LocalDateTime.now(), tokenInfo.tokenTimeout, ChronoUnit.SECONDS));
        vo.setUserId(userId.toString());
        // 清理临时token
        SaTempUtil.deleteToken(refreshTokenDto.getRefreshToken());
        return new Result<>(CodeEnum.SUCCESS.get(), vo, "访问令牌更新成功");
    }


    /**
     * 登出系统
     */
    @RequestMapping("logout")
    public Result<?> logout() {
        // 清理刷新token
        String userId = StpUtil.getLoginIdAsString();
        if (!StringUtils.hasText(userId)) {
            return new Result<>(CodeEnum.SUCCESS.get(), null, "系统登出成功");
        }
        // 清理角色缓存信息
        redisTemplate.delete(RedisCacheKey.permissionRole(StpUtil.getTokenValue()));
        SaTempUtil.deleteToken(userId);
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


}
