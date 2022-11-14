package com.yz.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;

/**
 * @author : gaohan
 * @date : 2022/8/30 23:41
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 配置认证方式等
        super.configure(auth);
    }

    @Autowired
    private CustomizeAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private CustomizeAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private CustomizeAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private CustomizeLogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private CustomizeSessionInformationExpiredStrategy sessionInformationExpiredStrategy;

    /**
     * http相关的配置，包括登入登出、异常处理、会话管理等
     *
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // super.configure(http);
        http.csrf().disable()
                .authorizeRequests()
                // 放行接口
                .antMatchers("/oauth/**", "/maintain/**").permitAll()
                .anyRequest().authenticated()
                .and()
                // 登入
                .formLogin().permitAll()
                // 指定登录接口uri
                .loginProcessingUrl("/oauth/login")
                // 指定登录账号参数名
                .usernameParameter("userAccount")
                // 指定登录密码参数名
                .passwordParameter("userPassword")
                // 登录成功处理逻辑
                // .successHandler(authenticationSuccessHandler)
                // 登录失败处理逻辑
                // .failureHandler(authenticationFailureHandler)
                .and()
                // 登出
                .logout().permitAll()
                // 登出成功处理逻辑
                // .logoutSuccessHandler(logoutSuccessHandler)
                // .and()
                // 异常处理(权限拒绝、登录失效等)
                // .exceptionHandling()
                // 匿名用户访问无权限资源时的异常处理    (加了该处理方案之后，未登录时就不会自动跳到登录页了)
                // .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                // 会话管理
                .sessionManagement()
                // 同一账号同时登录最大用户数
                .maximumSessions(1)
                // 会话信息过期策略会话信息过期策略(账号被挤下线)
                .expiredSessionStrategy(sessionInformationExpiredStrategy);
    }
}
