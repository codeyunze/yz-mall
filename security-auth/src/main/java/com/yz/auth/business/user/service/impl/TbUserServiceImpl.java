package com.yz.auth.business.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yz.auth.business.permission.entity.TbPermission;
import com.yz.auth.business.permission.service.TbPermissionService;
import com.yz.auth.business.user.dao.TbUserDao;
import com.yz.auth.business.user.entity.TbUser;
import com.yz.auth.business.user.service.TbUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户表(TbUser)表服务实现类
 *
 * @author makejava
 * @since 2023-02-14 23:35:56
 */
@Service("tbUserService")
public class TbUserServiceImpl extends ServiceImpl<TbUserDao, TbUser> implements TbUserService, UserDetailsService {

    @Autowired
    private TbPermissionService permissionService;

    @Override
    public TbUser getByUsername(String username) {
        LambdaQueryWrapper<TbUser> query = new LambdaQueryWrapper<>();
        query.eq(TbUser::getUsername, username).last(" limit 1");
        return baseMapper.selectOne(query);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        TbUser user = this.getByUsername(username);
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user != null) {
            List<TbPermission> permissions = permissionService.getByUserId(user.getId());
            permissions.forEach(permission -> {
                if (permission != null && StringUtils.hasText(permission.getEnname())) {
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission.getEnname());
                    authorities.add(grantedAuthority);
                }
            });
            return new User(user.getUsername(), user.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("用户名不存在");
        }
    }
}

