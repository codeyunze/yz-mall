package com.yz.mall.sys.business.task;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 消息队列配置信息
 * @author yunze
 * @date 2025/1/21 17:29
 */
@Configuration
public class RabbitMQConfig implements RabbitListenerConfigurer {

    private final ApplicationContext applicationContext;
    private final ConnectionFactory connectionFactory;

    public RabbitMQConfig(ApplicationContext applicationContext, ConnectionFactory connectionFactory) {
        this.applicationContext = applicationContext;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar rabbitListenerEndpointRegistrar) {
        Map<String, AbstractTaskService> baseServices = applicationContext.getBeansOfType(AbstractTaskService.class);
        baseServices.forEach((beanName, service) -> {
            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
            container.setQueues(new Queue(service.setMessageQueue() + "_start", true));
            container.setAcknowledgeMode(AcknowledgeMode.AUTO);
            container.setMessageListener(service::start);
            container.start();

            SimpleMessageListenerContainer containerEnd = new SimpleMessageListenerContainer(connectionFactory);
            containerEnd.setQueues(new Queue(service.setMessageQueue() + "_end", true));
            containerEnd.setAcknowledgeMode(AcknowledgeMode.AUTO);
            // service.end(new String(message.getBody()));
            containerEnd.setMessageListener(service::end);
            containerEnd.start();
        });
    }
}
