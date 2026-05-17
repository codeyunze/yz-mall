package com.yz.mall.poc.sw.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.poc.sw.vo.ClientConfigVo;
import com.yz.mall.poc.sw.vo.VehicleInfoVo;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * @author yunze
 * @since 2026/5/15 15:02
 */
public class ControlRangeFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public ControlRangeFilter(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest((HttpServletRequest) request);

        String vin = wrapped.getHeader("vin");
        if (!StringUtils.hasText(vin)) {
            chain.doFilter(wrapped, response);
            return;
        }

        String clientId = wrapped.getHeader("clientId");

        ClientConfigVo clientConfig = getClientConfig(clientId);
        if (clientConfig == null) {
            // todo yunze 通过openfeign获取配置信息
            writeJsonError(httpResponse, FORBIDDEN.value(), "clientId 没有访问授权");
            return;
        }
        // 车辆信息
        VehicleInfoVo vehicleInfo = getVehicleInfo(vin);
        if (vehicleInfo == null) {
            writeJsonError(httpResponse, FORBIDDEN.value(), "车辆信息不存在");
            return;
        }

        // 先进行车厂校验
        if (!Objects.equals(vehicleInfo.getFactoryCode(), clientConfig.getFactoryCode())) {
            writeJsonError(httpResponse, FORBIDDEN.value(), "无权跨车厂操控");
            return;
        }

        if (clientConfig.getVins() != null && !clientConfig.getVins().isEmpty()) {
            if (!clientConfig.getVins().contains(vin)) {
                writeJsonError(httpResponse, FORBIDDEN.value(), "VIN 不在授权范围内");
                return;
            }
            chain.doFilter(wrapped, response);
            return;
        }

        if (clientConfig.getVehicleModes() != null && !clientConfig.getVehicleModes().isEmpty()) {
            if (!clientConfig.getVehicleModes().contains(vehicleInfo.getVehicleModelCode())) {
                writeJsonError(httpResponse, FORBIDDEN.value(), "车辆型号不在授权范围内");
                return;
            }
            chain.doFilter(wrapped, response);
            return;
        }

        if (clientConfig.getVehicleSeries() != null && !clientConfig.getVehicleSeries().isEmpty()) {
            if (!clientConfig.getVehicleSeries().contains(vehicleInfo.getVehicleSeriesName())) {
                writeJsonError(httpResponse, FORBIDDEN.value(), "车辆系列不在授权范围内");
                return;
            }
            chain.doFilter(wrapped, response);
            return;
        }

        // 没有配置限制车系、车型、车辆，直接放行
        // TODO: 2026/5/15 yunze 认为应该需要添加限制车系、车型、车辆，不应该默认全部通过
        chain.doFilter(wrapped, response);
    }

    /** Servlet Filter 不参与 Spring MVC 异常解析，不能直接抛 ResponseStatusException，否则会变成 500。 */
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

    private VehicleInfoVo getVehicleInfo(String vin) {
        // 从redis缓存获取
        String cacheKey = "vehicle-info:vin:" + vin;
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
}
