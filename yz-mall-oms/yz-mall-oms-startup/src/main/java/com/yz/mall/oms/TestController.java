package com.yz.mall.oms;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author yunze
 * @date 2024/11/5 17:42
 */
@RefreshScope
@RestController
@RequestMapping()
public class TestController {

    @Value("${test}")
    private String value;

    @Resource
    private SaTokenConfig value2;

    @SaIgnore
    @RequestMapping("login")
    public String login(String username, String password) {
        // 此处仅作模拟示例，真实项目需要从数据库中查询数据进行比对
        if("root,admin".contains(username) && "a1234567".equals(password)) {
            StpUtil.login(username);
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return tokenInfo.getTokenValue();
        }
        return "登录失败";
    }

    @RequestMapping("/a")
    public String a() {
        System.err.println(value);
        return value;
    }

    @SaCheckLogin
    @RequestMapping("/b")
    public String b() {
        return "b";
    }

    @SaCheckPermission("api-c")
    @RequestMapping("/c")
    public String c() {
        return "c";
    }

    @SaIgnore
    @RequestMapping("/d")
    public String d() {
        return value2.getTokenPrefix();
    }
}
