package com.yz.mall.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.mall.sys.dto.SysPendingTasksAddDto;
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
    public Long save(SysPendingTasksAddDto dto) {
        SysPendingTasks bo = new SysPendingTasks();
        BeanUtils.copyProperties(dto, bo);
        // bo.setId(IdUtil.getSnowflakeNextId());
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
        LambdaQueryWrapper<SysPendingTasks> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(new Page<>(filter.getCurrent(), filter.getSize()), queryWrapper);
    }
}

