package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysMsgRetry;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息重试记录表(SysMsgRetry)表数据库访问层
 *
 * @author yunze
 * @since 2025-01-20
 */
@Mapper
public interface SysMsgRetryMapper extends BaseMapper<SysMsgRetry> {

}

