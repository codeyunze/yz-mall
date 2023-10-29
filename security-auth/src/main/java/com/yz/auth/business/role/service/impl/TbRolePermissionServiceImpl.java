package com.yz.auth.business.role.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.auth.business.role.dao.TbRolePermissionDao;
import com.yz.auth.business.role.entity.TbRolePermission;
import com.yz.auth.business.role.service.TbRolePermissionService;
import org.springframework.stereotype.Service;

/**
 * 角色权限表(TbRolePermission)表服务实现类
 *
 * @author yunze
 * @since 2023-02-14 23:47:45
 */
@Service("tbRolePermissionService")
public class TbRolePermissionServiceImpl extends ServiceImpl<TbRolePermissionDao, TbRolePermission> implements TbRolePermissionService {

}

