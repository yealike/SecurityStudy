package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //密码一般都是经过加密处理的
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //下面是明文密码123经过加密后的密文密码
        String password = passwordEncoder.encode("123");
        System.out.println(password);
        //设置登录密码为密文密码
        auth.inMemoryAuthentication().withUser("lucy").password(password).roles();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
