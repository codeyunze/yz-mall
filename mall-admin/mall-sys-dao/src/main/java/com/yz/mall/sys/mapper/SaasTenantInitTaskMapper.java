package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SaasTenantInitTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * SaaS-租户初始化任务表(SaasTenantInitTask)数据库访问层
 *
 * @author yunze
 * @since 2026-04-20 00:00:00
 */
@Mapper
public interface SaasTenantInitTaskMapper extends BaseMapper<SaasTenantInitTask> {
}
