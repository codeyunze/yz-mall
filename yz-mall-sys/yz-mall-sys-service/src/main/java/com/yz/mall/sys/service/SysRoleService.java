package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysRoleAddDto;
import com.yz.mall.sys.dto.SysRoleQueryDto;
import com.yz.mall.sys.dto.SysRoleUpdateDto;
import com.yz.mall.sys.entity.SysRole;
import com.yz.tools.PageFilter;

import javax.validation.Valid;

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
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysRole> page(PageFilter<SysRoleQueryDto> filter);

}

