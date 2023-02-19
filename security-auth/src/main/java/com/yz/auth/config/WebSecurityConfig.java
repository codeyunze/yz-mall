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
 * @Description TODO
 * @Author yunze
 * @Date 2023/2/13 23:56
 * @Version 1.0
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // @Autowired
    // private UserDetailsServiceImpl userDetailsService;

    @Resource(name = "tbUserService")
    private TbUserServiceImpl userService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // super.configure(auth);
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                // .loginPage("/login.html")
                // .loginProcessingUrl("/user/login")
                // .successForwardUrl("/main")
                // .failureForwardUrl("/toerror")
                .and().authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable();

        // http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
