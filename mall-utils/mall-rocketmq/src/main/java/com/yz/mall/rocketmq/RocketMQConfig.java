package com.yz.mall.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.autoconfigure.RocketMQProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.util.StringUtils;

/**
 * RocketMQ 公共配置：输出连接信息。
 *
 * @author yunze
 * @date 2026/9/3 星期四 23:03
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RocketMQProperties.class)
public class RocketMQConfig {

    private final RocketMQProperties rocketMQProperties;

    public RocketMQConfig(RocketMQProperties rocketMQProperties) {
        this.rocketMQProperties = rocketMQProperties;
    }

    /**
     * 应用就绪后打印 RocketMQ 连接信息。
     */
    @EventListener(ApplicationReadyEvent.class)
    @ConditionalOnProperty(prefix = "rocketmq", name = "name-server")
    public void logRocketMQConfig() {
        if (!StringUtils.hasText(rocketMQProperties.getNameServer())) {
            return;
        }
        log.info(">>>>>>>>>>> rocketmq config ready. nameServer={}, producerGroup={}, consumerGroup={}, accessChannel={}",
                rocketMQProperties.getNameServer(),
                rocketMQProperties.getProducer().getGroup(),
                rocketMQProperties.getConsumer().getGroup(),
                rocketMQProperties.getAccessChannel());
    }
}
