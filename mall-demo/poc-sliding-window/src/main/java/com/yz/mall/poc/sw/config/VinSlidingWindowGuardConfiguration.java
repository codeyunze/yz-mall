package com.yz.mall.poc.sw.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.poc.sw.guard.SwVinGuardProperties;
import com.yz.mall.poc.sw.ratelimit.VinSlidingWindowGateService;
import com.yz.mall.poc.sw.web.ControlRangeFilter;
import com.yz.mall.poc.sw.web.VinSlidingWindowGuardFilter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 滑动窗口 VIN 校验过滤器：以 {@link Bean} 注册，便于统一装配与顺序控制。
 */
@Configuration
public class VinSlidingWindowGuardConfiguration {

    @Bean
    public FilterRegistrationBean<VinSlidingWindowGuardFilter> vinSlidingWindowGuardFilterRegistration(
            SwVinGuardProperties guardProperties,
            VinSlidingWindowGateService vinGate,
            ObjectMapper objectMapper,
            StringRedisTemplate stringRedisTemplate) {
        VinSlidingWindowGuardFilter filter = new VinSlidingWindowGuardFilter(
                guardProperties, vinGate, objectMapper, stringRedisTemplate);
        FilterRegistrationBean<VinSlidingWindowGuardFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 50);
        // 拦截/poc/*路径下的请求
        reg.addUrlPatterns("/poc/*");
        return reg;
    }

    @Bean
    public FilterRegistrationBean<ControlRangeFilter> controlRangeFilterRegistration(
            StringRedisTemplate stringRedisTemplate,
            ObjectMapper objectMapper) {
        ControlRangeFilter filter = new ControlRangeFilter(stringRedisTemplate, objectMapper);
        FilterRegistrationBean<ControlRangeFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 51);
        // 拦截/poc/*路径下的请求
        reg.addUrlPatterns("/poc/*");
        return reg;
    }
}
