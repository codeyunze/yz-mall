package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SaasTenantDatasourceHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户服务数据源配置历史表(SaasTenantDatasourceHistory)数据库访问层
 *
 * @author yunze
 * @since 2026-04-20 00:00:00
 */
@Mapper
public interface SaasTenantDatasourceHistoryMapper extends BaseMapper<SaasTenantDatasourceHistory> {
}
