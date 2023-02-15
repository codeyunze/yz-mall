package com.yz.auth.business.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.auth.business.permission.entity.TbPermission;

import java.util.List;

/**
 * 权限表(TbPermission)表服务接口
 *
 * @author makejava
 * @since 2023-02-14 23:47:14
 */
public interface TbPermissionService extends IService<TbPermission> {

    /**
     * 根据用户Id查询用户权限
     * @param userId 用户Id
     * @return 用户权限
     */
    List<TbPermission> getByUserId(Long userId);
}

