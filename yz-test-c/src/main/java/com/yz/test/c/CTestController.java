package com.yz.test.c;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * @author yunze
 * @date 2024/11/5 17:42
 */
@RestController
@RequestMapping()
public class CTestController {

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
        return "a";
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
}
