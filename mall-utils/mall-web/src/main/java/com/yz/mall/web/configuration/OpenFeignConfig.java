package com.yz.mall.web.configuration;


import com.yz.mall.base.HeaderConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

/**
 * OpenFeign 请求拦截：透传必要 header。
 * <p>
 * 底层 HTTP 客户端由 mall-web 引入的 {@code feign-hc5} 自动切换为 Apache HttpClient 5；
 * 可通过配置 {@code spring.cloud.openfeign.httpclient.hc5.enabled=false} 关闭。
 *
 * @author yunze
 * @date 2025/1/22 14:34
 */
@Slf4j
@Configuration
public class OpenFeignConfig implements RequestInterceptor {

    @Resource
    private feign.Client client;

    @PostConstruct
    public void printFeignClient() {
        Object target = client;
        // 有 LoadBalancer 时拆一层
        if (target.getClass().getName().contains("LoadBalancer")) {
            try {
                var field = target.getClass().getDeclaredField("delegate");
                field.setAccessible(true);
                target = field.get(target);
            } catch (Exception ignored) {
            }
        }
        log.info("Feign Client = {}", target.getClass().getName());
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return;
        }
        Map<String, String> headers = getHeaders(Objects.requireNonNull(getHttpServletRequest()));
        for (String headerName : headers.keySet()) {
            requestTemplate.header(headerName, getHeaders(getHttpServletRequest()).get(headerName));
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        } catch (Exception e) {
            log.error("获取请求头失败", e);
            return null;
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        // 需要携带的 header
        Set<String> matchHeader = new HashSet<>();
        matchHeader.add("authorization");
        matchHeader.add("cookie");
        matchHeader.add("x-real-ip");
        matchHeader.add("host");
        matchHeader.add("x-forwarded-host");
        matchHeader.add("accept");
        matchHeader.add("sec-ch-ua-platform");
        matchHeader.add(HeaderConstants.TRACE_ID_HEADER);
        matchHeader.add(HeaderConstants.USER_IP_HEADER);

        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            if (matchHeader.contains(key)) {
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }
        return map;
    }
}
