package com.yz.auth.config;

import com.yz.auth.business.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @ClassName WebSecurityConfig
 * @Description TODO
 * @Author yunze
 * @Date 2023/2/13 23:56
 * @Version 1.0
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected UserDetailsService userDetailsService() {
        return userDetailsService;
    }
}
