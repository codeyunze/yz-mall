package com.yz.mall.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.vo.SysMenuSlimVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统-菜单资源表(SysMenu)表数据库访问层
 *
 * @author yunze
 * @since 2024-11-21 23:29:01
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询请求者所拥有访问权限的菜单
     *
     * @param roleIds 角色Id列表
     * @return 指定角色所拥有的菜单（菜单有去重）
     */
    List<SysMenuSlimVo> selectMenuSlimByRoleIds(@Param("roleIds") List<Long> roleIds);

}

