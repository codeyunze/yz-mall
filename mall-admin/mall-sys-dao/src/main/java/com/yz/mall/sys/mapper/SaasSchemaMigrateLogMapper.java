package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SaasSchemaMigrateLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户 schema 增量迁移执行日志(SaasSchemaMigrateLog)数据库访问层
 *
 * @author yunze
 * @since 2026-08-25
 */
@Mapper
public interface SaasSchemaMigrateLogMapper extends BaseMapper<SaasSchemaMigrateLog> {
}
