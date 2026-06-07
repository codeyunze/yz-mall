package com.yz.mall.poc.sw.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.redis.core.StringRedisTemplate;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.poc.sw.guard.SwUserIdGuardProperties;
import com.yz.mall.poc.sw.vo.ClientConfigVo;
import com.yz.mall.poc.sw.vo.VehicleInfoVo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author yunze
 * @since 2026/5/15 15:02
 */
public class ControlRangeFilter extends OncePerRequestFilter {

    private final SwUserIdGuardProperties guardProperties;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    public ControlRangeFilter(SwUserIdGuardProperties guardProperties, StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.guardProperties = guardProperties;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if (!guardProperties.isEnabled()) {
            return true;
        }
        List<String> uris = guardProperties.getUris();
        if (uris.isEmpty()) {
            return true;
        }
        String path = pathWithinApplication(request);
        if (!StringUtils.hasText(path)) {
            path = "/";
        }
        return firstMatchingPattern(path, uris) == null;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws IOException, ServletException {
        CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(request);

        String userId = wrapped.getHeader("userId");
        if (!StringUtils.hasText(userId)) {
            chain.doFilter(wrapped, response);
            return;
        }

        String clientId = wrapped.getHeader("clientId");

        ClientConfigVo clientConfig = getClientConfig(clientId);
        if (clientConfig == null) {
            // todo yunze 通过openfeign获取配置信息
            writeJsonError(response, FORBIDDEN.value(), "clientId 没有访问授权");
            return;
        }
        // 车辆信息
        VehicleInfoVo vehicleInfo = getVehicleInfo(userId);
        if (vehicleInfo == null) {
            writeJsonError(response, FORBIDDEN.value(), "车辆信息不存在");
            return;
        }

        // 先进行车厂校验
        if (!Objects.equals(vehicleInfo.getFactoryCode(), clientConfig.getFactoryCode())) {
            writeJsonError(response, FORBIDDEN.value(), "无权跨车厂操控");
            return;
        }

        if (clientConfig.getUserIds() != null && !clientConfig.getUserIds().isEmpty()) {
            if (!clientConfig.getUserIds().contains(userId)) {
                writeJsonError(response, FORBIDDEN.value(), "UserId 不在授权范围内");
                return;
            }
            chain.doFilter(wrapped, response);
            return;
        }

        if (clientConfig.getVehicleModes() != null && !clientConfig.getVehicleModes().isEmpty()) {
            if (!clientConfig.getVehicleModes().contains(vehicleInfo.getVehicleModelCode())) {
                writeJsonError(response, FORBIDDEN.value(), "车辆型号不在授权范围内");
                return;
            }
            chain.doFilter(wrapped, response);
            return;
        }

        if (clientConfig.getVehicleSeries() != null && !clientConfig.getVehicleSeries().isEmpty()) {
            if (!clientConfig.getVehicleSeries().contains(vehicleInfo.getVehicleSeriesName())) {
                writeJsonError(response, FORBIDDEN.value(), "车辆系列不在授权范围内");
                return;
            }
            chain.doFilter(wrapped, response);
            return;
        }

        // 没有配置限制车系、车型、车辆，直接放行
        // TODO: 2026/5/15 yunze 认为应该需要添加限制车系、车型、车辆，不应该默认全部通过
        chain.doFilter(wrapped, response);
    }


    private void writeJsonError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.resetBuffer();
        response.setStatus(statusCode);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> map = new HashMap<>();
        map.put("code", 11235);
        map.put("msg", message);
        response.getWriter().write(objectMapper.writeValueAsString(map));
    }

    private VehicleInfoVo getVehicleInfo(String userId) {
        // 从redis缓存获取
        String cacheKey = "vehicle-info:userId:" + userId;
        String cacheValue = stringRedisTemplate.boundValueOps(cacheKey).get();
        if (!StringUtils.hasText(cacheValue)) {
            return null;
        }
        try {
            return objectMapper.readValue(cacheValue, VehicleInfoVo.class);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    private ClientConfigVo getClientConfig(String clientId) {
        String cacheValue = stringRedisTemplate.boundValueOps("config:clientId:" + clientId).get();
        if (!StringUtils.hasText(cacheValue)) {
            return null;
        }
        try {
            return objectMapper.readValue(cacheValue, ClientConfigVo.class);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    private String firstMatchingPattern(String path, List<String> patterns) {
        for (String p : patterns) {
            if (StringUtils.hasText(p) && pathMatcher.match(p, path)) {
                return p;
            }
        }
        return null;
    }

    private String pathWithinApplication(HttpServletRequest request) {
        return urlPathHelper.getPathWithinApplication(request);
    }
}
