package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.dto.PageBean;
import com.guat.mynewsapp.dto.Result;
import com.guat.mynewsapp.entity.Notification;
import com.guat.mynewsapp.service.NotificationService;
import com.guat.mynewsapp.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 站内通知接口（需登录）
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /** 我的通知分页列表 */
    @GetMapping("/list")
    public Result<PageBean<Notification>> list(@RequestParam(defaultValue = "1") int pageNum,
                                               @RequestParam(defaultValue = "10") int pageSize,
                                               HttpServletRequest request) {
        return Result.success(notificationService.listMyNotifications(
                UserContext.requireUserId(request), pageNum, pageSize));
    }

    /** 未读数（红点） */
    @GetMapping("/unread-count")
    public Result<Long> unreadCount(HttpServletRequest request) {
        return Result.success(notificationService.unreadCount(UserContext.requireUserId(request)));
    }

    /** 标记已读：notificationId 为空 = 全部已读 */
    @PostMapping("/read")
    public Result<Void> read(@RequestParam(required = false) Long notificationId,
                             HttpServletRequest request) {
        notificationService.markRead(UserContext.requireUserId(request), notificationId);
        return Result.success();
    }
}
