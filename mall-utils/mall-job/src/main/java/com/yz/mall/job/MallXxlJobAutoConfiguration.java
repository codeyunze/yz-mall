package com.yz.mall.job;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

/**
 * XXL-JOB 执行器自动配置。
 * <p>
 * 端口优先级：显式 {@code xxl.job.executor.port} > {@code server.port + port-offset}（offset 默认 100）。
 * 地址优先级：显式 {@code xxl.job.executor.address} > {@code http://{ip}:{executorPort}}。
 * 当配置了 {@code xxl.job.admin.addresses} 时生效。
 */
@AutoConfiguration
@ConditionalOnClass(XxlJobSpringExecutor.class)
@ConditionalOnProperty(prefix = "xxl.job.admin", name = "addresses")
public class MallXxlJobAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MallXxlJobAutoConfiguration.class);

    @Value("${server.port}")
    private int serverPort;

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.admin.accessToken:}")
    private String accessToken;

    @Value("${xxl.job.admin.timeout:3}")
    private int timeout;

    @Value("${xxl.job.executor.appname:${spring.application.name}}")
    private String appname;

    @Value("${xxl.job.executor.address:}")
    private String address;

    @Value("${xxl.job.executor.ip:}")
    private String ip;

    @Value("${xxl.job.executor.port:0}")
    private int configuredPort;

    /**
     * 未显式配置 port 时，相对 server.port 的偏移量
     */
    @Value("${xxl.job.executor.port-offset:100}")
    private int portOffset;

    @Value("${xxl.job.executor.logpath:./logs/xxl-job}")
    private String logPath;

    @Value("${xxl.job.executor.logretentiondays:30}")
    private int logRetentionDays;

    /**
     * 初始化 XXL-JOB 执行器。
     *
     * @return 执行器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public XxlJobSpringExecutor xxlJobExecutor() {
        int executorPort = configuredPort > 0 ? configuredPort : serverPort + portOffset;
        String executorAddress = StringUtils.hasText(address) ? address : "http://" + ip + ":" + executorPort;
        log.info(">>>>>>>>>>> xxl-job config init. serverPort={}, executorPort={}, address={}", serverPort, executorPort, executorAddress);
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(adminAddresses);
        executor.setAppname(appname);
        executor.setAddress(executorAddress);
        executor.setIp(ip);
        executor.setPort(executorPort);
        executor.setAccessToken(accessToken);
        executor.setTimeout(timeout);
        executor.setLogPath(logPath);
        executor.setLogRetentionDays(logRetentionDays);
        return executor;
    }
}
