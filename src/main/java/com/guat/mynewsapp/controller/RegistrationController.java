package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.dto.Result;
import com.guat.mynewsapp.entity.User;
import com.guat.mynewsapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "注册接口", description = "新用户注册功能，无需授权即可访问")
@Slf4j
@RestController
@RequestMapping("/api")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "新用户注册",
            description = "传入用户基本信息完成注册，默认生成普通用户角色；用户名需唯一，密码建议符合复杂度要求"
    )
    @Parameter(
            name = "user",
            description = "注册信息（表单/JSON格式），核心字段：username（用户名）、password（密码）",
            required = true,
            example = "{\"username\":\"lisi\",\"password\":\"654321\",\"role\":0}"
    )
    @PostMapping("registration")
    public Result addUser(@RequestBody User user) {
        log.info("user={}", user);
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()
                || user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return Result.error("用户名或密码不能为空");
        }
        userService.addUser(user);
        return Result.success("注册成功！");
    }
}
