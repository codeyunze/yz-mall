package com.yz.mall.web.configuration;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * openfeign请求时，携带header信息
 *
 * @author yunze
 * @date 2025/1/22 14:34
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Map<String, String> headers = getHeaders(Objects.requireNonNull(getHttpServletRequest()));
        for (String headerName : headers.keySet()) {
            requestTemplate.header(headerName, getHeaders(getHttpServletRequest()).get(headerName));
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        Set<String> matchHeader = new HashSet<>();
        matchHeader.add("authorization");
        matchHeader.add("cookie");
        matchHeader.add("x-real-ip");
        matchHeader.add("host");
        matchHeader.add("x-forwarded-host");
        matchHeader.add("accept");
        matchHeader.add("sec-ch-ua-platform");

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
