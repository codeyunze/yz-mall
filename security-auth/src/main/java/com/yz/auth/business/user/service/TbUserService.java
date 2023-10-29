package com.yz.auth.business.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yz.auth.business.user.entity.TbUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 用户表(TbUser)表服务接口
 *
 * @author yunze
 * @since 2023-02-14 23:35:56
 */
public interface TbUserService extends IService<TbUser> {

    /**
     * 根据用户名获取用户信息
     * @param username 用户账号
     * @return 用户信息
     */
    TbUser getByUsername(String username);
}

