package com.yz.auth.business.baseRole.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.auth.business.baseRole.entity.BaseRole;

import java.util.List;

/**
 * <p>
 * 基础-角色表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2022-09-13
 */
public interface BaseRoleService extends IService<BaseRole> {

    /**
     * 根据用户ID查询该用户的角色
     * @param userId 用户ID
     * @return 该用户所拥有的角色列表
     */
    List<String> queryUserOwnedRoleCodes(String userId);
}
