package com.example.security;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.security.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
