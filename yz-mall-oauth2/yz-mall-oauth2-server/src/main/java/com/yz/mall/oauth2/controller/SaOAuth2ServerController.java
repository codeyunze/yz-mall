package com.yz.mall.oauth2.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.oauth2.config.SaOAuth2Config;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Handle;
import cn.dev33.satoken.oauth2.logic.SaOAuth2Util;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.advice.exception.BusinessException;
import com.yz.mall.oauth2.vo.AuthLoginVo;
import com.yz.mall.user.service.BaseUserService;
import com.yz.mall.user.vo.BaseUserVo;
import com.yz.tools.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Sa-OAuth2 Server端 控制器
 *
 * @author yunze
 * @date 2024/8/6 星期二 23:40
 */
@RestController
public class SaOAuth2ServerController {

    private static final Logger log = LoggerFactory.getLogger(SaOAuth2ServerController.class);

    @Autowired
    private BaseUserService baseUserService;

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    // 处理所有OAuth相关请求
    @RequestMapping("/oauth2/*")
    public Object request() {
        System.out.println("------- 进入请求: " + SaHolder.getRequest().getUrl());
        return SaOAuth2Handle.serverRequest();
    }

    /**
     * Sa-OAuth2 定制化配置
     *
     * @param cfg
     */
    @Autowired
    public void setSaOAuth2Config(SaOAuth2Config cfg) {
        cfg.
                // 未登录的视图
                        setNotLoginView(() -> {
                    return new ModelAndView("login.html");
                }).
                // 登录处理函数
                        setDoLoginHandle((name, pwd) -> {
                    BaseUserVo userVo = baseUserService.get(name);
                    if ("sa".equals(name) && "123456".equals(pwd)) {
                        StpUtil.login(10001);
                        return Result.success();
                    } else if (userVo != null && userVo.getPassword().equals(pwd)) {
                        StpUtil.login(userVo.getId());

                        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                        AuthLoginVo vo = new AuthLoginVo();
                        vo.setUsername(name);
                        vo.setNickname(name);
                        vo.setAccessToken(tokenInfo.tokenValue);
                        vo.setExpires(LocalDateTimeUtil.offset(LocalDateTime.now(), tokenInfo.tokenTimeout, ChronoUnit.SECONDS));
                        // 刷新令牌有效期1天
                        vo.setRefreshToken(SaTempUtil.createToken(name, 86400));
                        vo.setRoles(Collections.singletonList("admin"));
                        vo.setAvatar("https://avatars.githubusercontent.com/u/56632502");

                        try {
                            String str = new ObjectMapper().writeValueAsString(userVo);
                            redisTemplate.opsForValue().set("mall:userinfo:" + userVo.getId(), str);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                        return Result.success(vo);
                    }
                    throw new BusinessException("账号名或密码错误");
                    /*if ("sa".equals(name) && "123456".equals(pwd)) {
                        StpUtil.login(10001);
                        return SaResult.ok();
                    }
                    return SaResult.error("账号名或密码错误");*/
                }).
                // 授权确认视图
                        setConfirmView((clientId, scope) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("clientId", clientId);
                    map.put("scope", scope);
                    log.info("授权确认视图 - 客户端id: {} , 授权范围: {}", clientId, scope);
                    return new ModelAndView("confirm.html", map);
                })
        ;
    }

    // 全局异常拦截
    /*@ExceptionHandler
    public SaResult handlerException(Exception e) {
        e.printStackTrace();
        return SaResult.error(e.getMessage());
    }*/


    // ---------- 开放相关资源接口： Client端根据 Access-Token ，置换相关资源 ------------

    // 获取Userinfo信息：昵称、头像、性别等等
    @RequestMapping("/oauth2/userinfo")
    public SaResult userinfo() {
        // 获取 Access-Token 对应的账号id
        String accessToken = SaHolder.getRequest().getParamNotNull("access_token");
        Object loginId = SaOAuth2Util.getLoginIdByAccessToken(accessToken);
        System.out.println("-------- 此Access-Token对应的账号id: " + loginId);

        // 校验 Access-Token 是否具有权限: userinfo
        SaOAuth2Util.checkScope(accessToken, "userinfo");

        // 模拟账号信息 （真实环境需要查询数据库获取信息）
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("nickname", "shengzhang_");
        map.put("avatar", "http://xxx.com/1.jpg");
        map.put("age", "18");
        map.put("sex", "男");
        map.put("address", "山东省 青岛市 城阳区");
        return SaResult.data(map);
    }
}
