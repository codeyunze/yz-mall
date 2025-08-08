package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.yz.mall.sys.business.task.AbstractSysPendingTasksQueueConfig;
import com.yz.mall.sys.dto.InternalSysPendingTasksAddDto;
import com.yz.mall.sys.dto.SysPendingTasksQueryDto;
import com.yz.mall.sys.dto.SysPendingTasksUpdateDto;
import com.yz.mall.sys.entity.SysPendingTasks;
import com.yz.mall.sys.mapper.SysPendingTasksMapper;
import com.yz.mall.sys.service.SysPendingTasksService;
import com.yz.mall.base.IdDto;
import com.yz.mall.json.JacksonUtil;
import com.yz.mall.base.PageFilter;
import com.yz.mall.base.exception.BusinessException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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


    private final RabbitTemplate rabbitTemplate;

    public SysPendingTasksServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Long save(InternalSysPendingTasksAddDto dto) {
        SysPendingTasks bo = new SysPendingTasks();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);

        // 消息路由
        String routingKey = bo.getTaskCode().toLowerCase().replace(":", "_");

        try {
            String message = JacksonUtil.getObjectMapper().writeValueAsString(bo);
            rabbitTemplate.convertAndSend(AbstractSysPendingTasksQueueConfig.EXCHANGE_NAME, routingKey + "_start_key", message);
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

        // 消息路由
        String routingKey = tasks.getTaskCode().toLowerCase().replace(":", "_");

        try {
            String message = JacksonUtil.getObjectMapper().writeValueAsString(tasks);
            rabbitTemplate.convertAndSend(AbstractSysPendingTasksQueueConfig.EXCHANGE_NAME, routingKey + "_end_key", message);
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

