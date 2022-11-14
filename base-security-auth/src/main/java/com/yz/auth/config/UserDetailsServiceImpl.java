package com.yz.auth.config;

import com.yz.auth.baseRole.service.BaseRoleService;
import com.yz.auth.baseUser.entity.BaseUser;
import com.yz.auth.baseUser.service.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : gaohan
 * @date : 2022/9/13 00:25
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private BaseUserService baseUserService;

    @Autowired(required = false)
    private BaseRoleService baseRoleService;

    @Override
    public UserDetails loadUserByUsername(String userAccount) throws UsernameNotFoundException {
        BaseUser user = baseUserService.findOneByUserAccount(userAccount);
        if (user == null) {
            //throw exception inform front end not this user
            throw new UsernameNotFoundException("登录用户：" + userAccount + " 不存在");
        }
        // List<String> roleCodeList = userRepository.queryUserOwnedRoleCodes(userName);
        List<String> roleCodeList = baseRoleService.queryUserOwnedRoleCodes(user.getUserId());

        List<GrantedAuthority> authorities =
                roleCodeList.stream().map(e -> new SimpleGrantedAuthority(e)).collect(Collectors.toList());

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getUserAccount(), user.getUserPassword(), authorities);

        return userDetails;
    }


}
