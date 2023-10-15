package com.yz.auth.jms.producer;

import com.yz.common.Constants;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author : yunze
 * @date : 2022/6/27 23:43
 */
@Component
public class DirectProducer {

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    /**
     * 直接发送到队列，task模式直发模式
     *
     * @param message String 消息体信息
     * @throws UnsupportedEncodingException
     */
    public void sendMessage(String message) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN);
        messageProperties.setPriority(2);
        // 设置消息转换器，如json
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        rabbitTemplate.send(Constants.QUEUE_DIRECT, new Message(message.getBytes(StandardCharsets.UTF_8), messageProperties));
    }
}
