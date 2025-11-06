package com.yz.mall.oms;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yunze
 * @since 2025/8/7 00:25
 */
@Configuration
@ConditionalOnProperty
@ComponentScan({"com.yz.mall.oms"})
public class OmsCoreConfig {
}
