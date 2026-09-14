package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.dto.Result;
import com.guat.mynewsapp.entity.Report;
import com.guat.mynewsapp.service.ReportService;
import com.guat.mynewsapp.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 举报接口（帖子 + 评论）
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /** 提交举报（需登录），reportType 1帖子 2评论 */
    @PostMapping("/submit")
    public Result<Void> submit(HttpServletRequest request, @RequestBody Report report) {
        reportService.submitReport(UserContext.requireUserId(request), report);
        return Result.success();
    }
}
