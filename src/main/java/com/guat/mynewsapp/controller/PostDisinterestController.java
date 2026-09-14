package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.dto.Result;
import com.guat.mynewsapp.service.PostDisinterestService;
import com.guat.mynewsapp.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 帖子"不感兴趣"接口（个人 feed 过滤）
 */
@RestController
@RequestMapping("/api/post")
public class PostDisinterestController {

    @Autowired
    private PostDisinterestService postDisinterestService;

    /** 不感兴趣 / 取消（需登录） */
    @PostMapping("/disinterest")
    public Result<Map<String, Object>> disinterest(@RequestParam Integer postId, HttpServletRequest request) {
        return Result.success(postDisinterestService.toggle(
                UserContext.requireUserId(request), postId));
    }

    /** 我标记过"不感兴趣"的帖子 id 集合（需登录，feed 过滤用） */
    @GetMapping("/disinterest/ids")
    public Result<List<Long>> disinterestIds(HttpServletRequest request) {
        return Result.success(postDisinterestService.getMyDisinterestIds(
                UserContext.requireUserId(request)));
    }
}
