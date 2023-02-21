package com.yz.auth.config;

import com.yz.auth.business.user.service.impl.TbUserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

/**
 * @ClassName WebSecurityConfig
 * @Description 认证授权服务配置
 * @Author yunze
 * @Date 2023/2/13 23:56
 * @Version 1.0
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // @Autowired
    // private UserDetailsServiceImpl userDetailsService;

    @Resource
    private TbUserServiceImpl userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // super.configure(auth);
        // auth.userDetailsService(userService);
        auth.inMemoryAuthentication().withUser("yunze")
                .password(passwordEncoder().encode("123456")).roles("ADMIN")
                .and()
                .passwordEncoder(passwordEncoder());
    }

    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/login")
                .permitAll()
                .anyRequest().authenticated();

        http.formLogin().permitAll().loginProcessingUrl("/login");

        http.logout().permitAll();
        // 退出登录清理Cookie
        // http.logout().deleteCookies("JSESSIONID");

        // 服务不存储会话session
        // http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 会话管理
        http.sessionManagement()
                // 同一账号同时登录最大用户数
                .maximumSessions(1);
                // .maxSessionsPreventsLogin(true);
    }*/
}
