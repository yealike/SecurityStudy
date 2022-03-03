package com.example.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfigTest extends WebSecurityConfigurerAdapter {
    //注入UserDetailsService
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 通过这个类可以实现自定义登录页面
     * @param http 含有HttpSecurity的configure重载方法
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //自定义页面设置
        http.formLogin()
                .loginPage("/login.html")//登录页面设置
                .loginProcessingUrl("/user/login")//登录访问路径
                .defaultSuccessUrl("/test/index").permitAll()//登录成功后跳转路径
                .and().authorizeRequests()//定义哪些资源受保护哪些资源不受保护
                .antMatchers("/","/test/hello","user/login").permitAll()//设置哪些路径可以直接访问不需要认证
                .anyRequest().authenticated()//所有请求都可以访问
                .and().csrf().disable();//关闭csrf的防护模式
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
