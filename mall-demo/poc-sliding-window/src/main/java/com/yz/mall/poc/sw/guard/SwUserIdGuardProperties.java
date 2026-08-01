package com.yz.mall.poc.sw.guard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * 滑动窗口 UserId 校验过滤器：仅根据请求 URI（Ant 风格）决定是否做校验。
 * <p>
 * 列表为空表示不拦截任何请求；命中列表中<strong>第一条</strong>匹配的模式时执行校验，
 * Redis 桶名由该模式字符串规范化得到，不同路径模式之间计数隔离。
 */
@ConfigurationProperties(prefix = "sw.user-id-guard")
public class SwUserIdGuardProperties {

    /**
     * 关闭后过滤器不做校验（全放行）。
     * true: 开启；false: 关闭；默认：true
     */
    private boolean enabled = true;

    /**
     * Ant 风格 URI 模式；请求路径命中<strong>任意一条</strong>即做 UserId 滑动窗口校验（顺序决定多模式重叠时的桶名）。
     */
    private List<String> uris = new ArrayList<>();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris != null ? uris : new ArrayList<>();
    }
}
