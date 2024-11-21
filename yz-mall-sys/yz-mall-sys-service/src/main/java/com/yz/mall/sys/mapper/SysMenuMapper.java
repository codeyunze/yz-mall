package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统-菜单资源表(SysMenu)表数据库访问层
 *
 * @author yunze
 * @since 2024-11-21 23:29:01
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

}

