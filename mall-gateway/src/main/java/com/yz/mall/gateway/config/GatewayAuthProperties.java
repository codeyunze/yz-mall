package com.yz.mall.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关认证配置属性
 *
 * @author yunze
 * @since 2025-12-05
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.auth")
public class GatewayAuthProperties {

    /**
     * 白名单路径列表（不需要认证的接口）
     */
    private List<String> whiteList = new ArrayList<>();

    /**
     * 是否启用网关认证（默认启用）
     */
    private Boolean enabled = true;

    /**
     * 默认白名单路径
     */
    public GatewayAuthProperties() {
        // 登录、注册等认证相关接口
        whiteList.add("/authentication/login");
        whiteList.add("/authentication/register");
        whiteList.add("/authentication/refreshToken");
        
        // 健康检查、监控接口
        whiteList.add("/actuator/**");
        whiteList.add("/health");
        
        // 错误页面
        whiteList.add("/error/**");
        
        // XXL-Job 相关接口
        whiteList.add("/beat");
        whiteList.add("/idleBeat");
        whiteList.add("/kill");
        whiteList.add("/run");
        whiteList.add("/log");
        
        // 测试接口（可根据需要移除）
        whiteList.add("/sys/test/**");
        whiteList.add("/extend/**");
    }
}

