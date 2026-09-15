package com.guat.mynewsapp.controller;

import com.guat.mynewsapp.dto.PageBean;
import com.guat.mynewsapp.dto.ReportHandleResult;
import com.guat.mynewsapp.dto.ReportVO;
import com.guat.mynewsapp.dto.Result;
import com.guat.mynewsapp.service.AdminReportService;
import com.guat.mynewsapp.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端-举报审核接口（仅 role=1 管理员可用）
 */
@RestController
@RequestMapping("/api/admin")
public class AdminReportController {

    @Autowired
    private AdminReportService adminReportService;

    /** 举报列表（需管理员）：status 可空=全部 0待审核 1已下架 2已驳回 */
    @GetMapping("/reports")
    public Result<PageBean<ReportVO>> reports(@RequestParam(required = false) Integer status,
                                              @RequestParam(defaultValue = "1") int pageNum,
                                              @RequestParam(defaultValue = "10") int pageSize,
                                              HttpServletRequest request) {
        UserContext.requireAdmin(request);
        return Result.success(adminReportService.listReports(status, pageNum, pageSize));
    }

    /** 审核举报（需管理员）：action 1通过(下架目标) 2驳回 */
    @PostMapping("/report/handle")
    public Result<ReportHandleResult> handle(@RequestParam Long reportId,
                                             @RequestParam Integer action,
                                             @RequestParam(required = false) String remark,
                                             HttpServletRequest request) {
        UserContext.requireAdmin(request);
        return Result.success(adminReportService.handleReport(
                UserContext.requireUserId(request), reportId, action, remark));
    }
}
