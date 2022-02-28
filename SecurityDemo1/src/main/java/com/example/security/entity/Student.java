package com.example.security.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Student {
    private Integer id;
    private String username;
    private String password;
}
