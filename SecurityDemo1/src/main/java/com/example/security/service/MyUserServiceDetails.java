package com.example.security.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.security.entity.Student;
import com.example.security.mapper.StudentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userDetailsService")
public class MyUserServiceDetails implements UserDetailsService {
    @Autowired
    private StudentMapper studentMapper;

    /**
     *
     * @param username 得到表单传过来的用户名
     * @return 返回User对象
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println(username);


        //根据用户名查询数据库
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        Student student = studentMapper.selectOne(wrapper);

        if (student==null){
            throw new UsernameNotFoundException("用户名不存在");
        }

        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("role");

        return new User(student.getUsername(),new BCryptPasswordEncoder().encode(student.getPassword()),auths);
    }


}
