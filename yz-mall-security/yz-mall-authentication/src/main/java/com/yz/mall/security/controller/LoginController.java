package com.yz.mall.security.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.yz.mall.security.dto.AuthLoginDto;
import com.yz.tools.ApiController;
import com.yz.tools.Result;
import com.yz.tools.enums.CodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 身份认证接口
 * @author yunze
 * @date 2024/7/30 23:16
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class LoginController extends ApiController {


    /**
     * 登录接口
     */
    @PostMapping("login")
    public Result<SaTokenInfo> login(@RequestBody @Valid AuthLoginDto loginDto) {
        // 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对
        if("root,admin".contains(loginDto.getUsername()) && "123456".equals(loginDto.getPassword())) {
            StpUtil.login(loginDto.getUsername());

            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return success(tokenInfo);
        }
        return new Result<>(CodeEnum.AUTHENTICATION_ERROR.get(), null, "登录失败");
    }

    /**
     * 查询登录状态
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
