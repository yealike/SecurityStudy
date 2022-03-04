package com.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
public class LoginController {
    @RequestMapping("/login")
    public String login(){
        System.out.println("登录成功！");
        return "redirect:main.html";
    }
    @PostMapping("/success")
    public String success1(){
        System.out.println("登录成功");
        return "登录成功PostMapping";
    }

    @GetMapping("/success")
    public String success2(){
        System.out.println("登录成功");
        return "登录成功GetMapping";
    }

    @RequestMapping("toError")
    public String toError(){
        return "redirect:error.html";
    }
}
