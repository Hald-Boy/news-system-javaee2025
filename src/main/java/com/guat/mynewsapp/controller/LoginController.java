package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.dto.Result;
import com.guat.mynewsapp.entity.User;
import com.guat.mynewsapp.service.UserService;
import com.guat.mynewsapp.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "登录接口", description = "用户登录验证，成功后返回JWT令牌（用于后续接口授权）")
@Slf4j
@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "用户登录",
            description = "传入用户名和密码，验证通过后生成并返回JWT令牌；后续需携带该令牌（Authorization: Bearer {Token}）访问需授权的接口"
    )
    @Parameter(
            name = "user",
            description = "登录信息（JSON格式），仅需传入username和password字段",
            required = true,
            example = "{\"username\":\"zhangsan\",\"password\":\"123456\"}"
    )
    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        log.info("用户名和密码：{}",user);
        //用户输入的账号密码封装在emp对象里
        User u = userService.login(user);
        //如果查询到有对应的员工就生成JWT令牌
        if(u != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id",u.getId());
            claims.put("username",u.getUsername());
            claims.put("role",u.getRole());

            String jwt = JwtUtils.generateJwt(claims);
            return Result.success(jwt);
        }
        return Result.error("用户名或密码错误");
    }
}
