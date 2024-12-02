package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysUserRelationRoleBindDto;
import com.yz.mall.sys.dto.SysUserRelationRoleQueryDto;
import com.yz.mall.sys.dto.SysUserRelationRoleUpdateDto;
import com.yz.mall.sys.entity.SysOrg;
import com.yz.mall.sys.entity.SysRole;
import com.yz.mall.sys.entity.SysUser;
import com.yz.mall.sys.entity.SysUserRelationRole;
import com.yz.tools.PageFilter;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统-用户与组织关联角色表(SysUserRelationRole)表服务接口
 *
 * @author yunze
 * @since 2024-11-26 11:46:13
 */
public interface SysUserRelationRoleService extends IService<SysUserRelationRole> {

    /**
     * 用户/组织绑定角色
     *
     * @param dto 新增基础数据
     * @return 是否绑定成功
     */
    boolean bind(SysUserRelationRoleBindDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysUserRelationRole> page(PageFilter<SysUserRelationRoleQueryDto> filter);

    /**
     * 获取指定用户所拥有的角色Id
     *
     * @param relationId 指定用户Id {@link SysUser#getId()} 或 组织Id {@link SysOrg#getId()}
     * @return 用户所拥有的角色Id {@link SysRole#getId()}
     */
    List<Long> getRoleIdsByRelationId(Long relationId);

    /**
     * 获取指定用户所拥有的角色编码
     *
     * @param relationId 指定用户Id {@link SysUser#getId()} 或 组织Id {@link SysOrg#getId()}
     * @return 用户所拥有的角色Id {@link SysRole#getRoleCode()}
     */
    List<String> getRoleCodesByRelationId(Long relationId);
}

