package com.yz.auth.business.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.auth.business.user.dao.TbUserRoleDao;
import com.yz.auth.business.user.entity.TbUserRole;
import com.yz.auth.business.user.service.TbUserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户角色表(TbUserRole)表服务实现类
 *
 * @author makejava
 * @since 2023-02-14 23:48:14
 */
@Service("tbUserRoleService")
public class TbUserRoleServiceImpl extends ServiceImpl<TbUserRoleDao, TbUserRole> implements TbUserRoleService {

}

