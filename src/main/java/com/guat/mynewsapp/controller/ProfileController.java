package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.dto.PageBean;
import com.guat.mynewsapp.dto.PostCardVO;
import com.guat.mynewsapp.dto.Result;
import com.guat.mynewsapp.dto.UserProfileVO;
import com.guat.mynewsapp.service.ProfileService;
import com.guat.mynewsapp.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 个人主页接口
 */
@RestController
@RequestMapping("/api/user")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

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
}
