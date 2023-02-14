package com.yz.auth.business.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.auth.business.user.dao.TbUserDao;
import com.yz.auth.business.user.entity.TbUser;
import com.yz.auth.business.user.service.TbUserService;
import org.springframework.stereotype.Service;

/**
 * 用户表(TbUser)表服务实现类
 *
 * @author makejava
 * @since 2023-02-14 23:35:56
 */
@Service("tbUserService")
public class TbUserServiceImpl extends ServiceImpl<TbUserDao, TbUser> implements TbUserService {

}

