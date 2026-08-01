package com.yz.mall.poc.sw.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.poc.sw.guard.SwUserIdGuardProperties;
import com.yz.mall.poc.sw.ratelimit.UserIdSlidingWindowGateService;
import com.yz.mall.poc.sw.web.ControlRangeFilter;
import com.yz.mall.poc.sw.web.UserIdSlidingWindowGuardFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 滑动窗口 UserId 校验过滤器：以 {@link Bean} 注册，便于统一装配与顺序控制。
 */
@Configuration
public class UserIdSlidingWindowGuardConfiguration {

    @Bean
    public FilterRegistrationBean<UserIdSlidingWindowGuardFilter> userIdSlidingWindowGuardFilterRegistration(
            SwUserIdGuardProperties guardProperties,
            UserIdSlidingWindowGateService userIdGate,
            ObjectMapper objectMapper,
            StringRedisTemplate stringRedisTemplate) {
        UserIdSlidingWindowGuardFilter filter = new UserIdSlidingWindowGuardFilter(
                guardProperties, userIdGate, objectMapper, stringRedisTemplate);
        FilterRegistrationBean<UserIdSlidingWindowGuardFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 50);
        // 拦截/poc/*路径下的请求
        // reg.addUrlPatterns("/poc/*");
        return reg;
    }

    @Bean
    public FilterRegistrationBean<ControlRangeFilter> controlRangeFilterRegistration(
            SwUserIdGuardProperties guardProperties,
            StringRedisTemplate stringRedisTemplate,
            ObjectMapper objectMapper) {
        ControlRangeFilter filter = new ControlRangeFilter(guardProperties, stringRedisTemplate, objectMapper);
        FilterRegistrationBean<ControlRangeFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 51);
        // 拦截/poc/*路径下的请求
        // reg.addUrlPatterns("/poc/*");
        return reg;
    }
}
