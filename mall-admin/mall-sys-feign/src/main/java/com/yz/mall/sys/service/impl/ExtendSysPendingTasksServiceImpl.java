package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yz.mall.base.enums.CodeEnum;
import com.yz.mall.base.exception.FeignException;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.sys.AbstractSysPendingTasksQueueConfig;
import com.yz.mall.sys.dto.ExtendSysPendingTasksAddDto;
import com.yz.mall.sys.feign.ExtendSysPendingTaskFeign;
import com.yz.mall.sys.service.ExtendSysPendingTasksService;
import com.yz.mall.base.IdDto;
import com.yz.mall.base.Result;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

/**
 * 内部暴露service实现类: 任务待办
 *
 * @author yunze
 * @date 2025/1/22 13:46
 */
@Service
public class ExtendSysPendingTasksServiceImpl implements ExtendSysPendingTasksService {

    private final ExtendSysPendingTaskFeign extendSysPendingTaskFeign;

    /**
     * 待办任务 RocketMQ 发送模板
     */
    private final RocketMQTemplate rocketMQTemplate;

    public ExtendSysPendingTasksServiceImpl(ExtendSysPendingTaskFeign extendSysPendingTaskFeign
            , RocketMQTemplate rocketMQTemplate) {
        this.extendSysPendingTaskFeign = extendSysPendingTaskFeign;
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    public Long startTask(ExtendSysPendingTasksAddDto taskInfo) {
        // Result<Long> result = extendSysPendingTaskFeign.startTask(taskInfo);
        // if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
        //     throw new FeignException(result.getCode(), result.getMsg());
        // }
        // return result.getData();

        taskInfo.setTaskId(IdUtil.getSnowflakeNextId());

        // 消息路由（转换为 RocketMQ 的 Tag）
        String routingKey = taskInfo.getTaskCode().toLowerCase().replace(":", "_");

        try {
            String message = JacksonUtil.getObjectMapper().writeValueAsString(taskInfo);
            // RocketMQ 中使用 Topic:Tag 形式发送消息
            String destination = AbstractSysPendingTasksQueueConfig.TOPIC_NAME + ":" + routingKey + "_start_key";
            SendResult sendResult = rocketMQTemplate.syncSend(destination, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return taskInfo.getTaskId();
    }

    @Override
    public boolean endTask(Long taskId) {
        Result<Boolean> result = extendSysPendingTaskFeign.endTask(new IdDto(taskId));
        if (!CodeEnum.SUCCESS.get().equals(result.getCode())) {
            throw new FeignException(result.getCode(), result.getMsg());
        }
        return result.getData();
    }
}
