package com.guat.mynewsapp.service;

import com.guat.mynewsapp.dto.PageBean;
import com.guat.mynewsapp.dto.ReportHandleResult;
import com.guat.mynewsapp.dto.ReportVO;
import com.guat.mynewsapp.dto.Result;
import com.guat.mynewsapp.entity.Report;

public interface AdminReportService {

    /** 举报列表（管理端），status 可空=全部：0待审核 1已下架 2已驳回 */
    PageBean<ReportVO> listReports(Integer status, int pageNum, int pageSize);

    /** 审核举报：action 1通过(下架目标) 2驳回 */
    ReportHandleResult handleReport(Integer adminId, Long reportId, Integer action, String remark);
}
