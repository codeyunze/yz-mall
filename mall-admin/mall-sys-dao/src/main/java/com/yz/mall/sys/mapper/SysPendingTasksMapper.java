package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysPendingTasks;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统-待办任务(SysPendingTasks)表数据库访问层
 *
 * @author yunze
 * @since 2025-01-15 16:58:30
 */
@Mapper
public interface SysPendingTasksMapper extends BaseMapper<SysPendingTasks> {

}

