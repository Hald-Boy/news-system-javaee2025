package com.guat.mynewsapp.service;

import com.guat.mynewsapp.entity.Report;

public interface ReportService {

    /** 提交举报，reportType 1帖子 2评论 */
    void submitReport(Integer userId, Report report);
}
