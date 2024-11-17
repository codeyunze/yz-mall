package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysPermissionAddDto;
import com.yz.mall.sys.dto.SysPermissionQueryDto;
import com.yz.mall.sys.dto.SysPermissionUpdateDto;
import com.yz.mall.sys.entity.SysPermission;
import com.yz.tools.PageFilter;

import javax.validation.Valid;

/**
 * 系统-权限数据表(SysPermission)表服务接口
 *
 * @author yunze
 * @since 2024-11-17 20:08:25
 */
public interface SysPermissionService extends IService<SysPermission> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysPermissionAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysPermissionUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysPermission> page(PageFilter<SysPermissionQueryDto> filter);

}

