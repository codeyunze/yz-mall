package com.yz.auth.business.role.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.auth.business.role.dao.TbRoleDao;
import com.yz.auth.business.role.entity.TbRole;
import com.yz.auth.business.role.service.TbRoleService;
import org.springframework.stereotype.Service;

/**
 * 角色表(TbRole)表服务实现类
 *
 * @author yunze
 * @since 2023-02-14 23:46:49
 */
@Service("tbRoleService")
public class TbRoleServiceImpl extends ServiceImpl<TbRoleDao, TbRole> implements TbRoleService {

}

