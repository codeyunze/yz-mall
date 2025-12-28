package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yz.mall.base.IdDto;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.sys.AbstractSysPendingTasksQueueConfig;
import com.yz.mall.sys.dto.ExtendSysPendingTasksAddDto;
import com.yz.mall.sys.dto.SysPendingTasksQueryDto;
import com.yz.mall.sys.dto.SysPendingTasksUpdateDto;
import com.yz.mall.sys.entity.SysPendingTasks;
import com.yz.mall.sys.mapper.SysPendingTasksMapper;
import com.yz.mall.sys.service.SysPendingTasksService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 系统-待办任务(SysPendingTasks)表服务实现类
 *
 * @author yunze
 * @since 2025-01-15 16:58:30
 */
@Service
public class SysPendingTasksServiceImpl extends ServiceImpl<SysPendingTasksMapper, SysPendingTasks> implements SysPendingTasksService {


    /**
     * 待办任务 RocketMQ 发送模板
     */
    private final RocketMQTemplate rocketMQTemplate;

    public SysPendingTasksServiceImpl(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    @Override
    public Long save(ExtendSysPendingTasksAddDto dto) {
        SysPendingTasks bo = new SysPendingTasks();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);

        // 消息路由（转换为 RocketMQ 的 Tag）
        String routingKey = bo.getTaskCode().toLowerCase().replace(":", "_");

        try {
            String message = JacksonUtil.getObjectMapper().writeValueAsString(bo);
            // RocketMQ 中使用 Topic:Tag 形式发送消息
            String destination = AbstractSysPendingTasksQueueConfig.TOPIC_NAME + ":" + routingKey + "_start_key";
            rocketMQTemplate.convertAndSend(destination, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return bo.getId();
    }

    @Override
    public boolean update(SysPendingTasksUpdateDto dto) {
        SysPendingTasks bo = new SysPendingTasks();
        BeanUtils.copyProperties(dto, bo);
        bo.setUpdateTime(LocalDateTime.now());
        return baseMapper.updateById(bo) > 0;
    }

    @Override
    public boolean end(IdDto dto) {
        SysPendingTasks tasks = baseMapper.selectById(dto.getId());
        if (tasks == null) {
            throw new BusinessException("待办任务不存在");
        }
        tasks.setTaskStatus(1);
        tasks.setTaskNode("处理完毕");
        tasks.setTaskEndTime(LocalDateTime.now());
        int updated = baseMapper.updateById(tasks);

        // 消息路由（转换为 RocketMQ 的 Tag）
        String routingKey = tasks.getTaskCode().toLowerCase().replace(":", "_");

        try {
            String message = JacksonUtil.getObjectMapper().writeValueAsString(tasks);
            String destination = AbstractSysPendingTasksQueueConfig.TOPIC_NAME + ":" + routingKey + "_end_key";
            rocketMQTemplate.convertAndSend(destination, message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return updated > 0;
    }

    @Override
    public Page<SysPendingTasks> page(PageFilter<SysPendingTasksQueryDto> filter) {
        SysPendingTasksQueryDto queryFilter = filter.getFilter();
        LambdaQueryWrapper<SysPendingTasks> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(queryFilter.getTaskTitle()), SysPendingTasks::getTaskTitle, queryFilter.getTaskTitle());
        queryWrapper.eq(StringUtils.hasText(queryFilter.getTaskCode()), SysPendingTasks::getTaskCode, queryFilter.getTaskCode());
        queryWrapper.eq(queryFilter.getTaskStatus() != null, SysPendingTasks::getTaskStatus, queryFilter.getTaskStatus());
        queryWrapper.ge(queryFilter.getStartTimeFilter() != null, SysPendingTasks::getCreateTime, queryFilter.getStartTimeFilter());
        queryWrapper.le(queryFilter.getEndTimeFilter() != null, SysPendingTasks::getCreateTime, queryFilter.getEndTimeFilter());
        queryWrapper.orderByDesc(SysPendingTasks::getId);
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

