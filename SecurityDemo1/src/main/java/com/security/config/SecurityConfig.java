package com.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 在进行自定义登录逻辑验证的时候spring容器内必须要有PasswordEncoder的实例
 * 自定义登录页面，要继承WebSecurityConfigurerAdapter,重写其中参数为HttpSecurity的configure方法
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //表单登录验证
        http.formLogin()
                //当发现/login的时候认为是登录的，必须和表单提交的地址一样，去执行UserDetailsServiceImpl
                .loginProcessingUrl("/login")
                //自定义登录页
                .loginPage("/login.html")
                //登录成功后跳转页面必须是post请求
                .successForwardUrl("/success")
                //登录失败后跳转页面，也是POST请求
                .failureForwardUrl("/toError");

        //授权认证
        http.authorizeRequests()
                .antMatchers("/error.html").permitAll()
                ///login.html不需要被认证
                .antMatchers("/login.html").permitAll()
                //所有请求都必须被认证
                .anyRequest().authenticated();

        //关闭csrf验证
        http.csrf().disable();
    }

    @Bean
    public PasswordEncoder getPwd(){
        return new BCryptPasswordEncoder();
    }
}
