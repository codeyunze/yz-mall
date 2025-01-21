package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.InternalSysPendingTasksAddDto;
import com.yz.mall.sys.dto.SysPendingTasksQueryDto;
import com.yz.mall.sys.dto.SysPendingTasksUpdateDto;
import com.yz.mall.sys.mapper.SysPendingTasksMapper;
import com.yz.mall.sys.entity.SysPendingTasks;
import com.yz.mall.sys.service.SysPendingTasksService;
import com.yz.mall.web.common.IdDto;
import com.yz.mall.web.common.PageFilter;
import com.yz.mall.web.exception.BusinessException;
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

    @Override
    public Long save(InternalSysPendingTasksAddDto dto) {
        SysPendingTasks bo = new SysPendingTasks();
        BeanUtils.copyProperties(dto, bo);
        bo.setId(IdUtil.getSnowflakeNextId());
        baseMapper.insert(bo);
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
        return baseMapper.updateById(tasks) > 0;
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
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

