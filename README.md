# SpringSecurity学习笔记

Spring生态圈的安全框架，提供了一套Web应用安全性的完整解决方案；

学习SpringSecurity的前置知识：

1. 掌握Spring框架
2. 掌握SpringBoot使用
3. 掌握JavaWeb技术

安全方案的两个区域是“认证”和“授权”（或者访问控制），一般来说，Web应用的安全性包括用户认证(Authentication)和用户授权(Authorization)两个部分，这两点也是Spring Security重要核心功能。

（1）用户认证指的是：验证某个用户是否为系统中的合法主体，也就是说用户能否访问该系统。用户认证一般要求用户提供用户名和密码，系统通过校验用户名和密码来完成认证过程，通俗点说就是系统认为用户是否能登录。

（2）用户授权是指验证某个用户是否有权限执行某个操作，在一个系统中，不同用户所具有的权限是不同的。比如对一个文件来说，有的用户只能进行读取，而有的用户可以进行修改。一般来说，系统会为不同的用户分配不同的角色，而每个角色对应一系列的权限，通俗点讲就是系统判断用户是否有权去做某些事情。



# 一、快速入门

1. 创建spring boot工程
2. 引入相关依赖
3. 编写controller进行测试

Spring Boot的父工程依赖

```xml
<parent>
    <artifactId>spring-boot-starter-parent</artifactId>
    <groupId>org.springframework.boot</groupId>
    <version>2.2.1.RELEASE</version>
</parent>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
</dependencies>
```

编写controller类进行测试

```java
@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping("hello")
    public String add(){
        return "hello,security";
    }
}
```

启动主程序，我在application.yml中配置的端口时8888

```java
@SpringBootApplication
public class SecurityApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringApplication.class, args);
    }
}
```

此时浏览器访问服务器资源（http://localhost:8888/test/hello）时会自动跳出默认的登录页面。

![1645796181129](C:\Users\YH925\AppData\Roaming\Typora\typora-user-images\1645796181129.png)

默认用户名为user,密码在控制台自动生成：930b176e-c13d-4efc-a432-a64c93d5f50a

输入用户名和密码之后就可以正常访问服务器资源了



# 二、SpringSecurity常用的两个接口

## 1.1 UserDetailsService接口

**作用：**查询数据库用户名和密码的过程，当什么也没有配置的时候，账号和密码都是有SpringSecurity定义生成的。而在实际项目中账号和密码都是从数据库中查询出来的。所以我们要通过自定义逻辑控制认证逻辑。

自定义逻辑的时候，需要实现`UserDetailsService`接口

**用法：**

1. 创建类继承UsernamePasswordAuthenticationFilter,重写三个方法
2. 创建类实现UserDetailsService，编写查询数据过程，返回User对象，这个User对象是安全框架提供对象



## 1.2 PasswordEncoder

数据加密接口，用于返回User对象里面密码加密

```
BCryptPasswordEncoder是SpringSecurity官方推荐的密码解析器，平时多使用这个解析器。
BCryptPasswordEncoder是对bcrypt强散列方法的具体实现，是基于Hash算法实现的单向加密。可以通过strenth控制加密强度，默认10.
```

## 1.3 web权限方法

（1）认证

（2）授权

1.设置登录用户名和密码的方法有三种方式如下

第一种方式，通过配置文件

第二种方式，通过配置类

第三种方式，自定义编写实现类

1. 在application.yml中配置用户名和密码

```yaml
spring:
  security:
    user:
      name: ok
      password: ok666
      
#这样就完成了用户名和密码的配置，再次启动项目后，用户名和密码就变成了ok和ok666
```

2.通过配置类设置用户名和密码

注释掉配置文件中的用户名和密码的配置方案

使用配置类设置用户名和密码需要先创建PasswordEncoder的实例对象，要不然登录的时候会报错

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //密码一般都是经过加密处理的
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //下面是明文密码123经过加密后的密文密码
        String password = passwordEncoder.encode("123");
        //设置登录密码为密文密码
        auth.inMemoryAuthentication().withUser("lucy").password(password).roles();
    }
	//创建PasswordEncoder的实例对象
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
```

以上的两种方法看着都不是那么的实用，因为有关用户名与密码的操作一般都是放在数据库里面。

这个时候需要再次提出`UserDetailsService`接口，用于在数据库获取用户名和密码

使用方法：自定义实现类设置

第一步：创建配置类，设置使用哪个userDetailsService实现类

第二步：编写实现类，返回User对象，User对象有用户密码和操作权限

先将原有的配置类Configuration注释掉,新建配置类

```java
@Configuration
public class SecurityConfigTest extends WebSecurityConfigurerAdapter {
    //注入UserDetailsService
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
```

创建service类

```java
@Service("userDetailsService")
public class MyUserServiceDetails implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("role");
        return new User("mary",new BCryptPasswordEncoder().encode("123"),auths);
    }
}

```

自定义用户名和密码的第三种方式基本实现,现在进行一个增强版,从数据库获取用户名和密码

1.整合MyBatisPlus引入相关依赖

```xml
		<!--lombok依赖-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!--mybatis-plus依赖-->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.3.1.tmp</version>
        </dependency>
        <!--mysql依赖-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>
```

2.创建数据库和数据库表

```sql
create table test.student
(
    id int auto_increment primary key,
    username varchar(125) not null,
    password varchar(125) not null,
    constraint student_id_uindex unique (id)
);
```

3.编写Student表对应的实体类

```java
@Data
@ToString
public class Student {
    private Integer id;
    private String username;
    private String password;
}

```

4.整合MyBatis-Plus实现BaseMapper接口,MP实现单表操作超级简单

```java
@Mapper
public interface StudentMapper extends BaseMapper<Student> {
}
```

5.在MyUserDetailsService调用mapper里面的方法查询数据库进行用户认证





### 3.4 基于角色或权限进行访问控制

#### 3.4.1 hasAuthority方法

如果当前的主体具有指定的权限，则返回true,否则返回false

单词分析：has(有)__Authority(权限)，判定一个角色是否有权限

- 修改配置类，我的配置类名称为SecurityConfigTest

1. 在配置类设置当前访问地址有哪些权限

```java
//当前登录的用户，只有具有admins权限才可以访问这个路径
.antMatchers("/test/index").hasAuthority("admins")
```

2.在UserDetailsService,把返回的User对象设置权限

```java
//把返回的对象设置权限
List<GrantedAuthority> auths =
                AuthorityUtils.commaSeparatedStringToAuthorityList("admins");
return new User(student.getUsername(),
                new BCryptPasswordEncoder().encode(student.getPassword()),auths);
```

当把权限设置成别的如"abc"`AuthorityUtils.commaSeparatedStringToAuthorityList("abc");`

则返回如下信息，表示无权访问(状态码403)

> There was an unexpected error (type=Forbidden, status=403).
>
> Forbidden

当我们重新把权限设置成"admins"之后，访问成功，返回正确信息。

hasAuthority方法的缺点就是，只能对单个用户设置访问权限，无法一次性设置多个。

Service类信息概览

```java
@Service("userDetailsService")
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private StudentMapper studentMapper;

    /**
     * 这个表单会得到表单传入的用户名
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //调用userMapper中的方法，根据用户名查询数据库
        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        //前面的key值是数据库里面的字段值，第二个value值是前端传入的值
        wrapper.eq("username", username);
        Student student = studentMapper.selectOne(wrapper);
        //判断
        if (student == null) {//数据库没有用户名，认证失败
            throw new UsernameNotFoundException("用户名不存在！");
        }

        //把返回的对象设置权限
        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("admins");
        return new User(student.getUsername(),
                new BCryptPasswordEncoder()
                        .encode(student.getPassword()),
                auths);
    }
}
```

配置类信息概览

```java
@Configuration
public class SecurityConfigTest extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(password());
    }

    @Bean
    PasswordEncoder password() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()//自定义自己编写的登录页面
                .loginPage("/login.html")//登录页面设置
                .loginProcessingUrl("/user/login")//登录返回路径
                .defaultSuccessUrl("/test/index").permitAll()//登录成功之后跳转路径
                .and().authorizeRequests()
                .antMatchers("/","/test/hello","user/login").permitAll()//设置哪些路径可以直接访问，不需要认证
                //当前登录的用户，只有具有admins权限才可以访问这个路径
                .antMatchers("/test/index").hasAuthority("admins")
                .anyRequest().authenticated()
                .and().csrf().disable();//关闭csrf的防护
    }
}
```

#### 3.4.2 hasAnyAuthority

如果当前的主体有任何提供的角色(给定的作为一个逗号分隔的字符串列表)的话，返回true

这个方法的有点就是克服了hasAuthority方法只能设置当个主体的角色权限的缺点。

使用方法，替换配置类中的hasAuthority方法

```java
//设置多个角色以逗号分隔
.antMatchers("/test/index").hasAnyAuthority("admins,manager")
```

#### 3.4.3 hasRole方法

如果用户具备给定角色就允许访问，否则出现403。

如果当前主体具有指定角色，则返回true

- Service层改动,分配指定角色要以ROLE_开头

```java
//把返回的对象设置权限
        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("admins,ROLE_sale");
        return new User(student.getUsername(),
                new BCryptPasswordEncoder()
                        .encode(student.getPassword()),
                auths);
```

配置类改动

```java
@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()//自定义自己编写的登录页面
                .loginPage("/login.html")//登录页面设置
                .loginProcessingUrl("/user/login")//登录返回路径
                .defaultSuccessUrl("/test/index").permitAll()//登录成功之后跳转路径
                .and().authorizeRequests()
                .antMatchers("/","/test/hello","user/login").permitAll()//设置哪些路径可以直接访问，不需要认证
                //当前登录的用户，只有具有admins权限才可以访问这个路径
                //.antMatchers("/test/index").hasAuthority("admins")
                //设置多个角色以逗号分隔
                //.antMatchers("/test/index").hasAnyAuthority("admins,manager")
                .antMatchers("/test/index").hasRole("role")
                .anyRequest().authenticated()
                .and().csrf().disable();//关闭csrf的防护
    }
```

#### 3.4.4 hasAnyRole 方法不需要多说了......(找规律就行了)

#### 3.4.5 配置自定义 403(没有权限访问)  web页面

方法：在配置类中进行配置就行了

1. 新建没有权限的页面 unAuth.html