package com.test.security;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class SecurityTest {
    @Test
    public void contextLoads(){
        PasswordEncoder ps = new BCryptPasswordEncoder();
        String encode = ps.encode("123");
//        System.out.println(encode);
        System.out.println(ps.matches("123","$2a$10$wSTBKz/SkL.5dNQOYnn8z.wqIEZ0L50GDXGXNS5FfiBZl05rkFa6i"));
    }
}
