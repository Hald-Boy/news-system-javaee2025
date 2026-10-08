package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.dto.Result;
import com.guat.mynewsapp.entity.User;
import com.guat.mynewsapp.service.UserService;
import com.guat.mynewsapp.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    // 登录/注册都已经写到 UserController.java，新的登录/注册已换为手机号和密码/验证码登录，这个传统的登录接口已弃用（原注册接口同理）
    @Hidden
    @Operation(
            summary = "用户登录",
            description = "传入用户名和密码，验证通过后生成并返回JWT令牌；后续需携带该令牌（Authorization: Bearer {Token}）访问需授权的接口"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "成功，data 为 JWT 字符串"),
            @ApiResponse(responseCode = "400", description = "用户名或密码错误")
    })
    @PostMapping("/closeApi/login")
    public Result<String> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "登录信息（JSON格式），仅需传入username和password字段", required = true)
            @RequestBody User user) {
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
