package com.yz.mall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.user.entity.SysApplication;
import org.apache.ibatis.annotations.Mapper;

/**
 * 应用配置(SysApplication)表数据库访问层
 *
 * @author yunze
 * @since 2024-08-11 20:10:14
 */
@Mapper
public interface SysApplicationMapper extends BaseMapper<SysApplication> {

}

