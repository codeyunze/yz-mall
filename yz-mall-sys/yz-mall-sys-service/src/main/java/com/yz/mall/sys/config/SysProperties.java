package com.yz.mall.sys.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 系统管理服务-配置信息
 *
 * @author yunze
 * @date 2024/11/23 星期六 17:02
 */
@Data
@ConfigurationProperties(prefix = "yz.mall.sys")
public class SysProperties {

    /**
     * 超级管理员Id
     */
    private String superAdminId = "1858113817985785856";
}
