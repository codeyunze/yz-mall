package com.yz.auth.config;

import com.yz.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName CustomizeAuthorizationCodeServices
 * @Description TODO
 * @Author yunze
 * @Date 2022/12/4 15:26
 * @Version 1.0
 */
@Service
@Slf4j
public class CustomizeAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

    protected final ConcurrentHashMap<String, OAuth2Authentication> authorizationCodeStore = new ConcurrentHashMap<String, OAuth2Authentication>();

    @Autowired
    private RedisUtil redisUtil;

    protected final String PREFIX = "AUTHORIZATION_CODE";

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        redisUtil.insertOrUpdateByHours(PREFIX + ":" + code, authentication, 2);
        System.err.println("授权码：" + code);
        authorizationCodeStore.put(code, authentication);
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        redisUtil.remove(PREFIX + ":" + code);
        OAuth2Authentication auth = this.authorizationCodeStore.remove(code);
        System.err.println("移除授权码：" + code + "[" + auth + "]");
        return auth;
    }
}
