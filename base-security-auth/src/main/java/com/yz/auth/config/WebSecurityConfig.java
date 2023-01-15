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

/**
 * @author : gaohan
 * @date : 2022/8/30 23:41
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

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

    @Autowired
    private CustomizeUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        // if (authorizationCodeServices == null) {
        //     return new InMemoryAuthorizationCodeServices();
        // }
        return new CustomizeAuthorizationCodeServices();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // oauth2 密码模式需要拿到这个bean
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 配置认证方式等
        // super.configure(auth);
        // 实现UserDetailService接口获取用户信息
        auth.userDetailsService(userDetailsService);
    }

    /**
     * http相关的配置，包括登入登出、异常处理、会话管理等
     *
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // super.configure(http);
        http.csrf().disable();

        // 授权
        http.authorizeRequests()
                // 放行接口,不需要认证
                .antMatchers("/oauth/**", "/maintain/**", "/js/**", "/css/**", "/images/**").permitAll()
                .anyRequest().authenticated();

        // 自定义登录设置
        http.formLogin().permitAll()
                // 登录页面设置
                .loginPage("/login.html")
                .defaultSuccessUrl("/main.html")
                // 指定登录接口uri
                .loginProcessingUrl("/oauth/login")
                // 指定登录账号参数名
                .usernameParameter("userAccount")
                // 指定登录密码参数名
                .passwordParameter("userPassword")
        // 登录成功处理逻辑
        // .successHandler(authenticationSuccessHandler)
        // 登录失败处理逻辑
        .failureHandler(authenticationFailureHandler)
        ;

        // 自定义登出设置
        http.logout().permitAll()
        // 登出成功处理逻辑
        .logoutSuccessHandler(logoutSuccessHandler);

        // 自定义异常处理(权限拒绝、登录失效等)
        http.exceptionHandling()
        // 匿名用户访问无权限资源时的异常处理    (加了该处理方案之后，未登录时就不会自动跳到登录页了)
        .authenticationEntryPoint(authenticationEntryPoint);

        // 会话管理
        http.sessionManagement()
                // 同一账号同时登录最大用户数
                .maximumSessions(1)
        // 会话信息过期策略会话信息过期策略(账号被挤下线)
        // .expiredSessionStrategy(sessionInformationExpiredStrategy)
        ;
    }
}
