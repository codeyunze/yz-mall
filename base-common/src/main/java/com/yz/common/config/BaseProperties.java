package com.yz.common.config;

import com.yz.common.dto.SnowflakeDTO;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @ClassName ConcreteConfig
 * @Description 自定义基础属性配置文件
 * @Author yunze
 * @Date 2022/11/21 14:01
 * @Version 1.0
 */
@Component
@ConfigurationProperties(prefix = "base")
public class BaseProperties {

    /**
     * 雪花ID生成器配置信息
     */
    private SnowflakeDTO snowflake;
}
