package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统-权限数据表(SysPermission)表数据库访问层
 *
 * @author yunze
 * @since 2024-11-17 20:08:25
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

}

