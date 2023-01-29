package com.yz.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author : yunze
 * @date : 2023/1/29 23:36
 */
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // super.configure(http);

        http.formLogin().permitAll()
                .and().authorizeRequests()
                .antMatchers("/oauth/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .logout()
                .permitAll()
                .and()
                .csrf()
                .disable();
    }
}
