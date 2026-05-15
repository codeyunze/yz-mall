package com.yz.mall.poc.sw.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yz.mall.poc.sw.guard.SwVinGuardProperties;
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

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * 滑动窗口操作 VIN 数量校验
 * <p>
 * 仅按配置的 URI 决定是否做校验；命中则解析 JSON 并校验
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 50)
public class VinSlidingWindowGuardFilter extends OncePerRequestFilter {

    private final SwVinGuardProperties guardProperties;
    private final VinSlidingWindowGateService vinGate;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final UrlPathHelper urlPathHelper = new UrlPathHelper();

    public VinSlidingWindowGuardFilter(
            SwVinGuardProperties guardProperties,
            VinSlidingWindowGateService vinGate,
            ObjectMapper objectMapper) {
        this.guardProperties = guardProperties;
        this.vinGate = vinGate;
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
            String clientId = wrapped.getHeader("clientId");
            if (!StringUtils.hasText(clientId)) {
                throw new ResponseStatusException(BAD_REQUEST, "请求头clientId不能为空");
            }

            byte[] raw = wrapped.getCachedBody();
            if (raw.length == 0) {
                throw new ResponseStatusException(BAD_REQUEST, "JSON 请求体不能为空（需含 vins、maxVinCount、timeWindow）");
            }
            JsonNode root = objectMapper.readTree(raw);
            VinJsonBodySupport.VinPayload payload = VinJsonBodySupport.parse(root);
            vinGate.assertWithinVinWindow(clientId.strip(), payload.getVins(), payload.getMaxVinCount(), payload.getTimeWindow());
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
}
