package com.yz.mall.security.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.yz.mall.security.dto.AuthLoginDto;
import com.yz.tools.ApiController;
import com.yz.tools.Result;
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
@RestController
@RequestMapping("/auth")
public class LoginController extends ApiController {


    /**
     * 登录接口
     */
    @PostMapping("login")
    public Result<Boolean> login(@RequestBody @Valid AuthLoginDto loginDto) {
        // 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对
        if("root".equals(loginDto.getUsername()) && "123456".equals(loginDto.getPassword())) {
            StpUtil.login(10001);
            return success(Boolean.TRUE);
        }
        return success(Boolean.FALSE);
    }

    /**
     * 查询登录状态
     * @return true: 已登录    false: 未登录
     */
    @RequestMapping("isLogin")
    public Result<Boolean> isLogin() {
        return success(StpUtil.isLogin());
    }
}
