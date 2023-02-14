package com.yz.auth.business.permission.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.auth.business.permission.dao.TbPermissionDao;
import com.yz.auth.business.permission.entity.TbPermission;
import com.yz.auth.business.permission.service.TbPermissionService;
import org.springframework.stereotype.Service;

/**
 * 权限表(TbPermission)表服务实现类
 *
 * @author makejava
 * @since 2023-02-14 23:47:14
 */
@Service("tbPermissionService")
public class TbPermissionServiceImpl extends ServiceImpl<TbPermissionDao, TbPermission> implements TbPermissionService {

}

