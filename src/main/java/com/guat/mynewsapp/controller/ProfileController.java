package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.dto.*;
import com.guat.mynewsapp.entity.User;
import com.guat.mynewsapp.service.ProfileService;
import com.guat.mynewsapp.service.UserService;
import com.guat.mynewsapp.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 个人主页接口
 */
@RestController
@RequestMapping("/api/user")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserService userService;

    /**
     * 个人主页（无需登录，未登录 isFollowing=false；看自己也走这里）
     * @param userId 个人主页的所属id
     * @param request 查看主页那个人的id
     * @return
     */
    @GetMapping("/profile")
    public Result<UserProfileVO> profile(@RequestParam Integer userId, HttpServletRequest request) {
        return Result.success(profileService.getProfile(UserContext.getUserId(request), userId));
    }


    /**
     * 我的作品列表（无需登录）
     * @param userId 作品列表的主人id
     * @param pageNum 页码
     * @param pageSize 一页记录数
     * @return
     */
    @GetMapping("/posts")
    public Result<PageBean<PostCardVO>> posts(@RequestParam Integer userId,
                                              @RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(profileService.listUserPosts(userId, pageNum, pageSize));
    }

    /** 编辑个人资料（需登录）：昵称/头像/背景/简介/生日/所在地，生日传 yyyy-MM-dd 或空 */
    @PostMapping("/profile/update")
    public Result<UserInfo> updateProfile(@RequestBody UserInfo userinfo, HttpServletRequest request) {
        return Result.success(userService.updateProfile(UserContext.requireUserId(request), userinfo));
    }
}
