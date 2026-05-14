package com.yz.mall.poc.sw.web;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.poc.sw.guard.SwVinGuardProperties;
import com.yz.mall.poc.sw.ratelimit.VinPerSecondRedlineLimiter;
import com.yz.mall.poc.sw.ratelimit.VinSlidingWindowGateService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UrlPathHelper;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

/**
 * 滑动窗口 VIN 校验：<strong>Servlet Filter</strong> 实现（继承 {@link OncePerRequestFilter}），
 * <p>
 * Spring Boot 会将带 {@link Component} 的 {@link jakarta.servlet.Filter} Bean 注册进容器，对匹配请求在 DispatcherServlet 之前执行。
 * 仅按配置的 URI 模式决定是否做校验；命中则解析 JSON 并校验。同一 {@code clientId} 在所有 URI 下共用 Redis 计数（{@code sw:vin:{clientId}}）。
 * <p>
 * 硬编码红线（与请求体阈值无关）：同一 {@code clientId} 每自然秒累计 VIN 种类数不超过 10，经 Redis 原子计数；滑动窗口校验失败时会回滚该秒额度。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 50)
public class VinSlidingWindowGuardFilter extends OncePerRequestFilter {

    private final SwVinGuardProperties guardProperties;
    private final VinSlidingWindowGateService vinGate;
    private final VinPerSecondRedlineLimiter perSecondRedline;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    public VinSlidingWindowGuardFilter(
            SwVinGuardProperties guardProperties,
            VinSlidingWindowGateService vinGate,
            VinPerSecondRedlineLimiter perSecondRedline,
            ObjectMapper objectMapper) {
        this.guardProperties = guardProperties;
        this.vinGate = vinGate;
        this.perSecondRedline = perSecondRedline;
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
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        CachedBodyHttpServletRequest wrapped = new CachedBodyHttpServletRequest(request);

        try {
            byte[] raw = wrapped.getCachedBody();
            if (raw.length == 0) {
                throw new ResponseStatusException(BAD_REQUEST, "JSON 请求体不能为空（需含 clientId、vins、maxVinCount、timeWindow）");
            }
            JsonNode root = objectMapper.readTree(raw);
            // 核心参数准备
            VinJsonBodySupport.VinPayload payload = VinJsonBodySupport.parse(root);
            int distinctVinCount = distinctNonBlankVinCount(payload.getVins());
            Optional<Runnable> rollbackRedline = perSecondRedline.tryReserve(payload.getClientId(), distinctVinCount);
            if (rollbackRedline.isEmpty()) {
                throw new ResponseStatusException(
                        TOO_MANY_REQUESTS, "红线：同一 client 每自然秒累计 VIN 种类数不超过 10");
            }
            Runnable rollback = rollbackRedline.get();
            try {
                vinGate.assertWithinVinWindow(
                        payload.getClientId(), payload.getVins(), payload.getMaxVinCount(), payload.getTimeWindow());
            } catch (RuntimeException ex) {
                rollback.run();
                throw ex;
            }
        } catch (ResponseStatusException ex) {
            writeError(response, ex);
            return;
        } catch (JsonProcessingException ex) {
            writeError(response, new ResponseStatusException(BAD_REQUEST, "JSON 不合法"));
            return;
        }

        filterChain.doFilter(wrapped, response);
    }

    private void writeError(HttpServletResponse response, ResponseStatusException ex) throws IOException {
        response.resetBuffer();
        response.setStatus(ex.getStatusCode().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(java.util.Map.of("message", ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString())));
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

    /** 请求内去重后的非空 VIN 个数，与红线按「种类」累计语义一致 */
    private static int distinctNonBlankVinCount(List<String> vins) {
        if (vins == null || vins.isEmpty()) {
            return 0;
        }
        Set<String> set = new LinkedHashSet<>();
        for (String v : vins) {
            if (v != null && !v.isBlank()) {
                set.add(v);
            }
        }
        return set.size();
    }
}
