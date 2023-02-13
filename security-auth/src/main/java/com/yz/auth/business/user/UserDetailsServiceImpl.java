package com.yz.auth.business.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserDetailsServiceImpl
 * @Description TODO
 * @Author yunze
 * @Date 2023/2/13 23:51
 * @Version 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String pwd = passwordEncoder.encode("123456");
        UserDetails userDetails = User.withUsername("yunze").password(pwd).authorities("admin").build();
        return userDetails;
    }
}
