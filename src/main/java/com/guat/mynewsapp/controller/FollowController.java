package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.dto.PageBean;
import com.guat.mynewsapp.dto.Result;
import com.guat.mynewsapp.dto.UserCardVO;
import com.guat.mynewsapp.service.FollowService;
import com.guat.mynewsapp.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 关注 / 粉丝接口
 */
@RestController
@RequestMapping("/api/user")
public class FollowController {

    @Autowired
    private FollowService followService;

    /**
     * 关注 / 取消关注（需登录）
     * @param targetUserId 被关注的用户id
     * @param request 获取当前登录用户的id
     * @return
     */
    @PostMapping("/follow")
    public Result<Map<String, Object>> follow(@RequestParam Integer targetUserId, HttpServletRequest request) {
        return Result.success(followService.toggleFollow(UserContext.requireUserId(request), targetUserId));
    }


    /**
     * 关注状态（无需登录，未登录 isFollowing=false）
     * @param targetUserId 目标用户id
     * @param request 当前登录用户id
     * @return
     */
    @GetMapping("/follow/status")
    public Result<Map<String, Object>> followStatus(@RequestParam Integer targetUserId, HttpServletRequest request) {
        return Result.success(followService.getFollowStatus(UserContext.getUserId(request), targetUserId));
    }

    /** 关注列表（查看 userId 的关注列表，无需登录） */
    @GetMapping("/following")
    public Result<PageBean<UserCardVO>> following(@RequestParam Integer userId,
                                                  @RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  HttpServletRequest request) {
        return Result.success(followService.listFollowing(UserContext.getUserId(request), userId, pageNum, pageSize));
    }

    /** 粉丝列表（查看 userId 的粉丝列表，无需登录） */
    @GetMapping("/fans")
    public Result<PageBean<UserCardVO>> fans(@RequestParam Integer userId,
                                               @RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize,
                                               HttpServletRequest request) {
        return Result.success(followService.listFans(UserContext.getUserId(request), userId, pageNum, pageSize));
    }
}
