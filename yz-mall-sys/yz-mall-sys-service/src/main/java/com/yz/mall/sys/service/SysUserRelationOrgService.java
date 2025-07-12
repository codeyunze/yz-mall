package com.yz.mall.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.mall.sys.dto.SysUserRelationOrgAddDto;
import com.yz.mall.sys.dto.SysUserRelationOrgQueryDto;
import com.yz.mall.sys.dto.SysUserRelationOrgUpdateDto;
import com.yz.mall.sys.entity.SysUserRelationOrg;
import com.yz.mall.sys.vo.InternalSysUserRelationOrgVo;
import com.yz.mall.web.common.PageFilter;

import javax.validation.Valid;
import java.util.List;

/**
 * 系统-用户关联组织数据表(SysUserRelationOrg)表服务接口
 *
 * @author yunze
 * @since 2024-11-17 20:26:16
 */
public interface SysUserRelationOrgService extends IService<SysUserRelationOrg> {

    /**
     * 新增数据
     *
     * @param dto 新增基础数据
     * @return 主键Id
     */
    Long save(SysUserRelationOrgAddDto dto);

    /**
     * 更新数据
     *
     * @param dto 更新基础数据
     * @return 是否操作成功
     */
    boolean update(@Valid SysUserRelationOrgUpdateDto dto);

    /**
     * 分页查询
     *
     * @param filter 过滤条件
     * @return 分页列表数据
     */
    Page<SysUserRelationOrg> page(PageFilter<SysUserRelationOrgQueryDto> filter);

    /**
     * 获取指定组织下的所有用户Id
     *
     * @param orgId 指定的组织Id
     * @return 用户Id
     */
    List<Long> getUserIdByOrgId(Long orgId);

    /**
     * 获取指定用户加入的组织信息
     *
     * @param userId 用户信息
     * @return 用户加入的组织信息
     */
    List<InternalSysUserRelationOrgVo> getOrgByUserId(Long userId);
}

