package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysRoleAddDto;
import com.yz.mall.sys.dto.SysRoleQueryDto;
import com.yz.mall.sys.dto.SysRoleUpdateDto;
import com.yz.mall.sys.entity.SysMenu;
import com.yz.mall.sys.entity.SysRole;
import com.yz.mall.web.common.PageFilter;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统-角色数据表(SysRole)表服务接口
 *
 * @author yunze
 * @since 2024-11-17 18:15:15
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysRoleAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysRoleUpdateDto dto);

    /**
     * 更新指定角色的状态
     *
     * @param id 角色Id
     * @return 是否操作成功
     */
    boolean updateRoleStatusById(Long id);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysRole> page(PageFilter<SysRoleQueryDto> filter);

    /**
     * 列表查询
     *
     * @param filter 过滤条件
     * @return 列表数据
     */
    List<SysRole> list(SysRoleQueryDto filter);

    /**
     * 根据指定角色Id获取其拥有的菜单
     *
     * @param roleId 指定的角色Id
     * @return 菜单Id {@link SysMenu#getId()}
     */
    List<String> getRoleMenusByRoleId(Long roleId);
}

