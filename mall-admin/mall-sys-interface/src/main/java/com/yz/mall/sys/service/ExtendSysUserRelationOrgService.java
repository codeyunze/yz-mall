package com.yz.mall.sys.service;

import com.yz.mall.sys.vo.InternalSysUserRelationOrgVo;

import java.util.List;

/**
 * 内部开放接口: 系统管理-用户组织信息
 *
 * @author yunze
 * @since 2025/5/18 22:43
 */
public interface ExtendSysUserRelationOrgService {

    /**
     * 获取指定用户加入的组织信息
     *
     * @param userId 用户信息
     * @return 用户加入的组织信息
     */
    List<InternalSysUserRelationOrgVo> getOrgByUserId(Long userId);
}
