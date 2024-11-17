package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysRoleRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统-关联角色数据表(SysRoleRelation)表数据库访问层
 *
 * @author yunze
 * @since 2024-11-17 19:55:59
 */
@Mapper
public interface SysRoleRelationMapper extends BaseMapper<SysRoleRelation> {

}

