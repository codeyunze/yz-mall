package com.yz.mall.sys;

// import com.baomidou.dynamic.datasource.annotation.EnableDynamicDataSource;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yz.mall.sys.support.ContextPropagatingTaskExecutor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.core.support.DynamicTp;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yunze
 * @since 2025/7/7 18:09
 */
@Slf4j
// @EnableDynamicDataSourc
@EnableAsync
@EnableCaching
@Configuration
@EnableConfigurationProperties(SysProperties.class)
@ComponentScan({"com.yz.mall.sys"})
@MapperScan("com.yz.mall.sys.mapper")
public class SysCoreConfig {

    /**
     * 业务异步线程池 Bean 名称 / Dynamic TP threadPoolName
     */
    public static final String ASYNC_EXECUTOR = "mallSysAsyncExecutor";

    @Resource
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

    /**
     * 创建业务异步线程池；参数以 Nacos Dynamic TP 配置为准，此处为本地兜底默认值。
     * <p>
     * 使用 {@link ContextPropagatingTaskExecutor}：Dynamic TP 会替换内部 Executor，
     * Spring {@code TaskDecorator} 会失效，必须在提交入口透传请求上下文，否则
     * {@code @DS("#session.xxx")} 在异步线程会 NPE。
     *
     * @return Spring 异步任务执行器
     */
    @DynamicTp(ASYNC_EXECUTOR)
    @Bean(name = ASYNC_EXECUTOR)
    public ThreadPoolTaskExecutor mallSysAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ContextPropagatingTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("mall-sys-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    /**
     * 将 {@link #mallSysAsyncExecutor()} 注册为 {@code @Async} 默认执行器（避免配置类自注入造成循环依赖）。
     *
     * @param executor 业务异步执行器
     * @return 异步配置
     */
    @Bean
    public AsyncConfigurer mallSysAsyncConfigurer(@Qualifier(ASYNC_EXECUTOR) ThreadPoolTaskExecutor executor) {
        return new AsyncConfigurer() {
            @Override
            public Executor getAsyncExecutor() {
                return executor;
            }

            @Override
            public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
                return (ex, method, params) -> log.error("@Async 未捕获异常, method={}", method, ex);
            }
        };
    }
}
