package com.yz.mall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.user.entity.BaseUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 基础-用户(BaseUser)表数据库访问层
 *
 * @author yunze
 * @since 2024-06-16 23:25:56
 */
@Mapper
public interface BaseUserMapper extends BaseMapper<BaseUser> {

}

