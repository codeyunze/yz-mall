package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysRoleRelationAddDto;
import com.yz.mall.sys.dto.SysRoleRelationQueryDto;
import com.yz.mall.sys.dto.SysRoleRelationUpdateDto;
import com.yz.mall.sys.entity.SysRoleRelation;
import com.yz.tools.PageFilter;

import javax.validation.Valid;

/**
 * 系统-关联角色数据表(SysRoleRelation)表服务接口
 *
 * @author yunze
 * @since 2024-11-17 19:55:59
 */
public interface SysRoleRelationService extends IService<SysRoleRelation> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysRoleRelationAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysRoleRelationUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysRoleRelation> page(PageFilter<SysRoleRelationQueryDto> filter);

}

