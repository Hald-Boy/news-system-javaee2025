package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.dto.CommentCardVO;
import com.guat.mynewsapp.dto.PageBean;
import com.guat.mynewsapp.dto.PostCardVO;
import com.guat.mynewsapp.dto.Result;
import com.guat.mynewsapp.service.CollectService;
import com.guat.mynewsapp.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 收藏接口（帖子 + 评论）
 */
@RestController
@RequestMapping("/api/collect")
public class CollectController {

    @Autowired
    private CollectService collectService;

    /** 收藏 / 取消收藏（需登录），collectType 1帖子 2评论 */
    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggle(@RequestParam Integer collectType,
                                              @RequestParam Long targetId,
                                              HttpServletRequest request) {
        return Result.success(collectService.toggleCollect(
                UserContext.requireUserId(request), collectType, targetId));
    }

    /** 收藏状态（无需登录，未登录视为未收藏） */
    @GetMapping("/status")
    public Result<Map<String, Object>> status(@RequestParam Integer collectType,
                                              @RequestParam Long targetId,
                                              HttpServletRequest request) {
        return Result.success(collectService.getCollectStatus(
                UserContext.getUserId(request), collectType, targetId));
    }

    /** 我的收藏-帖子（需登录） */
    @GetMapping("/mine/posts")
    public Result<PageBean<PostCardVO>> minePosts(@RequestParam(defaultValue = "1") int pageNum,
                                                  @RequestParam(defaultValue = "10") int pageSize,
                                                  HttpServletRequest request) {
        return Result.success(collectService.listMyCollectedPosts(
                UserContext.requireUserId(request), pageNum, pageSize));
    }

    /** 我的收藏-评论（需登录） */
    @GetMapping("/mine/comments")
    public Result<PageBean<CommentCardVO>> mineComments(@RequestParam(defaultValue = "1") int pageNum,
                                                          @RequestParam(defaultValue = "10") int pageSize,
                                                          HttpServletRequest request) {
        return Result.success(collectService.listMyCollectedComments(
                UserContext.requireUserId(request), pageNum, pageSize));
    }
}
