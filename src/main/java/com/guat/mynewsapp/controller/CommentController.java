package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.entity.Comment;
import com.guat.mynewsapp.entity.PageBean;
import com.guat.mynewsapp.entity.Result;
import com.guat.mynewsapp.service.CommentService;
import com.guat.mynewsapp.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    /**
     * 查询某帖子的所有一级评论
     * @param id 传入的帖子ID
     * @return 返回所有一级评论
     */
    @GetMapping("/list")
    public Result getAllComment(Integer id) {

        PageBean pageBean = commentService.getCommentByID(id);
        return Result.success(pageBean);
    }


    /**
     * 根据帖子ID和父评论ID查找父评论的子评论
     * @param newsId 帖子ID
     * @param parentId 父评论ID
     * @return 返回封装好的子评论
     */
    @GetMapping("listChild")
    public Result getCommentChild(Integer newsId, Integer parentId) {

        PageBean pageBean = commentService.getChildComment(newsId, parentId);
        return Result.success(pageBean);
    }


    /**
     * 新增评论
     */
    @PostMapping("/add")
    public Result add(HttpServletRequest request, @RequestBody Comment comment) {

        // 从拦截器存入的request域拿userId
        Integer userIdInt = (Integer) request.getAttribute("userId");
        if (userIdInt == null) {
            return Result.error("用户未登录或Token无效");
        }
        /**
         * Integer转Long，适配comment的Long类型userId
         * 由于前期数据库表设计和现在新增的表约定不统一：
         * ⚠⚠⚠⚠⚠⚠⚠⚠⚠
         * ⚠⚠请求头拿到的userId是Integer，而Comment实体类的userId需要的是Long类型，要么改表要么类型转换。⚠⚠⚠⚠⚠
         * ⚠⚠⚠⚠⚠⚠⚠⚠⚠ ↓↓↓
         */
        Long userId = Long.valueOf(userIdInt);
        log.info("(帖子Id)newsID:{},(评论的父Id)parentId:{},(对谁说的)toUserId:{},(内容)content:{}", comment.getNewsId(), comment.getParentId(), comment.getToUserId(), comment.getContent());

        if(comment.getNewsId() == null || comment.getContent() == null || comment.getContent().trim().isEmpty()){
            return Result.error("评论内容不能为空");
        }
        //把评论的ID设置为当前登录的用户的ID
        comment.setUserId(userId);
        log.info("(谁评论的)uerId:{}", comment.getUserId());
        boolean res = commentService.addComment(comment);
        return res ? Result.success("评论成功"):Result.success("评论失败！");
    }

    /**
     * 删除评论接口
     *
     * @param id 评论的id
     * @param request 请求参数
     * @return res为T则返回删除成功，为F返回删除失败
     */
    @DeleteMapping("/del")
    public Result del(Long id, HttpServletRequest request) {

        // 从拦截器存入的request域拿userId
        Integer userIdInt = (Integer) request.getAttribute("userId");
        if (userIdInt == null) {
            return Result.error("用户未登录或Token无效");
        }
        //把请求头拿到的Integer类型id转为Comment需要的Long类型id
        Long userId = Long.valueOf(userIdInt);

        boolean res = commentService.delComment(id, userId);
        return res ? Result.success("删除成功！"):Result.success("删除失败！");
    }

    /** 点赞 / 取消点赞评论（需登录） */
    @PostMapping("/like")
    public Result like(@RequestParam Long commentId, HttpServletRequest request) {
        return Result.success(commentService.toggleLike(UserContext.requireUserId(request), commentId));
    }

    /** 不喜欢 / 取消不喜欢（折叠评论，需登录） */
    @PostMapping("/dislike")
    public Result dislike(@RequestParam Long commentId, HttpServletRequest request) {
        return Result.success(commentService.toggleDislike(UserContext.requireUserId(request), commentId));
    }
}
