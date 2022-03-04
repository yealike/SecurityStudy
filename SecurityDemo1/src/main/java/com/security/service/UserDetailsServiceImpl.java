package com.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 实现自定义登录逻辑还需要实现UserDetailsService接口
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 1.查询数据库判断用户名是否存在，如果不存在就会抛出UsernameNotFoundException
     * 2.把查询出来的密码（注册时已经加密过的）进行解析，或者直接把密码放入构造方法中
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!"admin".equals(username)){
            throw new UsernameNotFoundException("用户名不存在");
        }
        String password = passwordEncoder.encode("123");

        return new User(username,password, AuthorityUtils.commaSeparatedStringToAuthorityList("admin,normal"));
    }
}
