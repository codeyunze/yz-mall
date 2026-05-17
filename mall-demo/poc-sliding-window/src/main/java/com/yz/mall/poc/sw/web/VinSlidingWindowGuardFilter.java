package com.yz.mall.poc.sw.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.poc.sw.guard.SwVinGuardProperties;
import com.yz.mall.poc.sw.ratelimit.VinSlidingWindowGateService;
import com.yz.mall.poc.sw.vo.ClientConfigVo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UrlPathHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * 滑动窗口操作 VIN 数量校验
 * <p>
 * 仅按配置的 URI 决定是否做校验；命中则解析 JSON 并校验。
 * <p>
 * Bean 由 {@link com.yz.mall.poc.sw.config.VinSlidingWindowGuardConfiguration} 以 {@code FilterRegistrationBean} 注册并设定顺序。
 */
public class VinSlidingWindowGuardFilter extends OncePerRequestFilter {

    private final SwVinGuardProperties guardProperties;
    private final VinSlidingWindowGateService vinGate;
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    public VinSlidingWindowGuardFilter(
            SwVinGuardProperties guardProperties,
            VinSlidingWindowGateService vinGate,
            ObjectMapper objectMapper,
            StringRedisTemplate stringRedisTemplate) {
        this.guardProperties = guardProperties;
        this.vinGate = vinGate;
        this.objectMapper = objectMapper;
        this.stringRedisTemplate = stringRedisTemplate;
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
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(request);

        try {
            // 第三方标识
            String clientId = wrapped.getHeader("clientId");
            if (!StringUtils.hasText(clientId)) {
                throw new ResponseStatusException(BAD_REQUEST, "请求头clientId不能为空");
            }
            // 车辆标识
            String vin = wrapped.getHeader("vin");
            if (!StringUtils.hasText(vin)) {
                throw new ResponseStatusException(BAD_REQUEST, "请求头vin不能为空");
            }
            // TODO: 2026/5/15 yunze 需要调整到缓存里面去查询
            // 最大操控车限制数量
            int maxVinCount = Integer.parseInt(wrapped.getHeader("maxVinCount"));
            // 时间窗口（单位：秒）
            int timeWindow = Integer.parseInt(wrapped.getHeader("timeWindow"));


            // byte[] raw = wrapped.getCachedBody();
            // if (raw.length == 0) {
            //     throw new ResponseStatusException(BAD_REQUEST, "JSON 请求体不能为空（需含 vins、maxVinCount、timeWindow）");
            // }
            // JsonNode root = objectMapper.readTree(raw);
            // VinJsonBodySupport.VinPayload payload = VinJsonBodySupport.parse(root);
            // vinGate.assertWithinVinWindow(clientId.strip(), payload.getVins(), payload.getMaxVinCount(), payload.getTimeWindow());
            vinGate.assertWithinVinWindow(clientId.strip(), Collections.singletonList(vin), maxVinCount, timeWindow);
        } catch (ResponseStatusException ex) {
            writeError(response, ex);
            return;
        }

        filterChain.doFilter(wrapped, response);
    }

    private void writeError(HttpServletResponse response, ResponseStatusException ex) throws IOException {
        response.resetBuffer();
        response.setStatus(ex.getStatusCode().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(Map.of("message", ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString())));
    }

    /**
     * 配置列表中第一条与 path 匹配的 Ant 模式；无匹配时 {@link #shouldNotFilter} 已放行，此处不应为 null。
     */
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

    public ClientConfigVo getClientConfig(String clientId) {
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
