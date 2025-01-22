package com.yz.mall.sys.business.task;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.yz.mall.web.common.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 待办任务基类
 *
 * @author yunze
 * @date 2025/1/20 15:16
 */
@Slf4j
@Component
public abstract class AbstractTaskService {

    /**
     * 设置任务待办消息队列</br>
     *
     * @return 消息队列
     */
    protected abstract String setMessageQueue();

    /**
     * 待办任务开始时，业务执行逻辑
     */
    protected abstract void startTask(Long taskId, String businessId);

    /**
     * 待办任务结束时，业务执行逻辑
     *
     * @param taskId 任务Id
     */
    protected abstract void endTask(Long taskId, String businessId);

    /**
     * 监听待办开始消息
     *
     * @param message 消息信息
     */
    public void start(Message message) {
        // 接收到消息
        log.info("3-接收到消息后，准备执行业务的开始待办逻辑");
        Map map = null;
        try {
            map = JacksonUtil.getObjectMapper().readValue(new String(message.getBody()), Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        startTask(Long.parseLong(map.get("id").toString()), map.get("businessId").toString());
    }

    /**
     * 监听待办结束消息
     *
     * @param message 消息信息
     */
    public void end(Message message) {
        // 接收到消息
        log.info("3-接收到消息后，准备执行业务的结束待办逻辑");
        Map map = null;
        try {
            map = JacksonUtil.getObjectMapper().readValue(new String(message.getBody()), Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        endTask(Long.parseLong(map.get("id").toString()), map.get("businessId").toString());
    }

}
