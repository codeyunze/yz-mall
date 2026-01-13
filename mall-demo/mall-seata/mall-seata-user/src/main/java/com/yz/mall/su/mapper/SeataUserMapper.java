package com.yz.mall.su.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.su.entity.SeataUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分布式事务-用户表(SeataUser)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-18 00:00:00
 */
@Mapper
public interface SeataUserMapper extends BaseMapper<SeataUser> {

}