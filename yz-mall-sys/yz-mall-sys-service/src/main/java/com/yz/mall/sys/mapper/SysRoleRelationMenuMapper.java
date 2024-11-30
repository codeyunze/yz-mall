package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysRoleRelationMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统-角色关联菜单表(SysRoleRelationMenu)表数据库访问层
 *
 * @author yunze
 * @since 2024-11-28 12:58:05
 */
@Mapper
public interface SysRoleRelationMenuMapper extends BaseMapper<SysRoleRelationMenu> {

}

