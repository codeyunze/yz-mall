package com.yz.auth.config;

import com.yz.redis.util.RedisRepository;
import com.yz.redis.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName CustomizeAuthorizationCodeServices
 * @Description 自定义授权码
 * @Author yunze
 * @Date 2022/12/4 15:26
 * @Version 1.0
 */
@Service
@Slf4j
public class CustomizeAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

    // protected final ConcurrentHashMap<String, OAuth2Authentication> authorizationCodeStore = new ConcurrentHashMap<String, OAuth2Authentication>();

    @Autowired
    private RedisUtil redisUtil;

    protected final String PREFIX = "AUTHORIZATION_CODE";

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        // redisUtil.insertOrUpdateByHours(redisKey(code), authentication, 2);
        System.err.println("授权码：" + code);
        // authorizationCodeStore.put(code, authentication);
        redisUtil.setExpire(redisKey(code), authentication, 2, TimeUnit.HOURS, RedisSerializer.java());
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        // Object o = redisUtil.get(PREFIX + ":" + code);
        // redisUtil.remove(PREFIX + ":" + code);
        // OAuth2Authentication auth = this.authorizationCodeStore.remove(code);
        // System.err.println("移除授权码：" + code + "[" + o + "]");
        // return JSON.toJavaObject(JSONObject.parseObject(JSON.toJSONString(o)), OAuth2Authentication.class);

        String codeKey = redisKey(code);
        OAuth2Authentication token = (OAuth2Authentication) redisUtil.get(codeKey, RedisSerializer.java());
        redisUtil.remove(codeKey);
        return token;
    }

    /**
     * redis中 code key的前缀
     * @param code 授权码
     * @return key
     */
    private String redisKey(String code) {
        return PREFIX + ":" + code;
    }
}
