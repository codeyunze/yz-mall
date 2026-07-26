package com.yz.mall.sys;

// import com.baomidou.dynamic.datasource.annotation.EnableDynamicDataSource;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author yunze
 * @since 2025/7/7 18:09
 */
@Slf4j
// @EnableDynamicDataSourc
@EnableCaching
@Configuration
@EnableConfigurationProperties(SysProperties.class)
@ComponentScan({"com.yz.mall.sys"})
@MapperScan("com.yz.mall.sys.mapper")
public class SysCoreConfig {

    @Autowired
    private SysProperties sysProperties;

    /**
     * 配置Caffeine本地缓存
     *
     * @return Caffeine Cache实例
     */
    @Bean
    public Cache<String, Object> caffeineCache() {
        log.info(">>>>>>>>>>> Caffeine cache config init.");
        SysProperties.CaffeineCache cacheConfig = sysProperties.getCaffeineCache();
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(cacheConfig.getMaximumSize());

        // 配置写入后过期时间
        if (cacheConfig.getExpireAfterWrite() != null && cacheConfig.getExpireAfterWrite() > 0) {
            caffeine.expireAfterWrite(cacheConfig.getExpireAfterWrite(), TimeUnit.SECONDS);
        }

        // 配置访问后过期时间
        if (cacheConfig.getExpireAfterAccess() != null && cacheConfig.getExpireAfterAccess() > 0) {
            caffeine.expireAfterAccess(cacheConfig.getExpireAfterAccess(), TimeUnit.SECONDS);
        }

        return caffeine.build();
    }
}
