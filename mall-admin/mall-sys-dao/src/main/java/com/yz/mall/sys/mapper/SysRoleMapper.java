package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统-角色数据表(SysRole)表数据库访问层
 *
 * @author yunze
 * @since 2024-11-17 18:15:15
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

}

