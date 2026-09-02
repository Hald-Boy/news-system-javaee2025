package com.guat.mynewsapp.controller;

import com.alibaba.fastjson2.JSON;
import com.guat.mynewsapp.annotation.RequiredRole;
import com.guat.mynewsapp.entity.*;
import com.guat.mynewsapp.service.NewsService;
import com.guat.mynewsapp.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 模块标签（Swagger UI 分类）
@Tag(name = "新闻管理接口", description = "提供新闻的增删改查功能，支持新闻图片的上传/修改/删除")
@Slf4j
@RestController
@RequestMapping("/api")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @Autowired
    private HttpServletRequest request; // 直接注入请求对象


    /**
     * 新增新闻（含图片）
     */
    @Operation(
            summary = "新增新闻",
            description = "添加一条新新闻，支持上传多张新闻图片；新闻基本信息为必填，图片为可选"
    )
    @Parameters({
            @Parameter(name = "news", description = "新闻基本信息（JSON格式），包含标题、内容、发布时间等", required = true),
            @Parameter(name = "newImages", description = "新闻配图（多张），格式支持jpg/png等，非必填", required = false)
    })
    @RequiredRole(1)
    @PostMapping
    public Result addNews(
            @RequestPart("news")  News news,
            @RequestPart(value = "newImages", required = false) MultipartFile[] newImages
    ) {
        log.info("news: {}", news);
        log.info("图片数量: {}", newImages != null ? newImages.length : 0);
        try {

            // ===== 从拦截器中获取当前用户ID =====
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录或Token无效");
            }

            // 设置新闻的创建者
            news.setUserId(userId);
            log.info("设置userID为: {}", userId);

            // ===== 新增：兼容无图片的情况（避免空指针）=====  2025/12/10  豆包
            MultipartFile[] finalNewImages = newImages == null ? new MultipartFile[0] : newImages;

            newsService.add(news, finalNewImages);
            return Result.success("新增新闻成功！");



        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("新增新闻失败！");
        }
    }




    /**
     * 修改新闻（含图片）
     */
    @Operation(
            summary = "修改新闻",
            description = "更新指定ID的新闻信息，支持新增图片、删除指定ID的旧图片；新闻ID为必填"
    )
    @Parameters({
            @Parameter(name = "id", description = "新闻唯一ID", required = true, example = "1"),
            @Parameter(name = "news", description = "更新后的新闻基本信息（JSON格式）", required = true),
            @Parameter(name = "newImages", description = "新增的新闻配图（多张），非必填", required = false),
            @Parameter(name = "deleteImageIds", description = "需要删除的旧图片ID列表，非必填", required = false, example = "[1,2]")
    })
    @RequiredRole(1)
    @PutMapping("/{id}")
    public Result update(
            @PathVariable Integer id,
            @RequestPart("news")  News news,
            @RequestPart(value = "newImages", required = false) MultipartFile[] newImages,
            @RequestParam(value = "deleteImageIds", required = false) String deleteImageIdsStr
    ) {
        log.info("更新新闻 id: {}, news: {}", id, news);
        log.info("删除图片ID: {}", deleteImageIdsStr);
        log.info("新增图片数量: {}", newImages != null ? newImages.length : 0);
        try {
            // ===== 从拦截器中获取当前用户ID（用于权限验证）=====
            Integer userId = (Integer) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录或Token无效");
            }

            news.setUserId(userId);  // 确保作者ID正确
            news.setId(id); // 绑定新闻ID

            // ===== 简化解析JSON字符串（适配前端传的[5,8]）=====
            List<Integer> deleteImageIds = new ArrayList<>();
            if (deleteImageIdsStr != null && !deleteImageIdsStr.trim().isEmpty() && !"null".equals(deleteImageIdsStr.trim())) {
                try {
                    // 用JSON工具解析（推荐FastJSON/Jackson，替换手动去括号）
                    deleteImageIds = JSON.parseArray(deleteImageIdsStr, Integer.class);
                    log.info("解析后待删除图片ID: {}", deleteImageIds);
                } catch (Exception e) {
                    log.error("解析删除图片ID失败，原始字符串: {}", deleteImageIdsStr, e);
                    return Result.error("删除图片ID格式错误，请传JSON数组（如[1,2]）");
                }
            }

            newsService.update(news, newImages, deleteImageIds);
            return Result.success("修改新闻成功！");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修改新闻失败！");
        }
    }




    /**
     * 根据ID查询新闻详情（含图片）
     */
    @Operation(
            summary = "查询新闻详情",
            description = "通过新闻ID获取单条新闻的完整信息，包含关联的图片列表"
    )
    @Parameter(name = "id", description = "新闻唯一ID", required = true, example = "1")
    @GetMapping("/{id}")
    public Result getById(@PathVariable Integer id) {
        try {
            News news = newsService.getById(id);
            return Result.success(news);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询新闻失败！");
        }
    }




    /**
     * 删除新闻（含图片）
     */
    @Operation(
            summary = "删除新闻",
            description = "根据新闻ID删除指定新闻，同时删除关联的所有图片"
    )
    @Parameter(name = "id", description = "新闻唯一ID", required = true, example = "1")
    @RequiredRole(1)
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        try {
            newsService.delete(id);
            return Result.success("删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除新闻失败！");
        }
    }




    /**
     * 分页查询新闻
     */
    @Operation(
            summary = "分页查询新闻",
            description = "支持按新闻标题模糊查询，默认页码1、每页10条数据"
    )
    @Parameters({
            @Parameter(name = "title", description = "新闻标题（模糊查询），非必填", required = false, example = "科技"),
            @Parameter(name = "pageNum", description = "页码，默认值1", required = false, example = "1"),
            @Parameter(name = "pageSize", description = "每页条数，默认值10", required = false, example = "10")
    })
    @GetMapping("/page")
    public Result pageQuery(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("title: {}, pageNum: {}, pageSize: {}", title, pageNum, pageSize);
        try {
            PageBean pageBean = newsService.pageQuery(title, pageNum, pageSize);
            return Result.success(pageBean);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("分页查询新闻失败！");
        }
    }


    /** 点赞 / 取消点赞（需登录） */
    @PostMapping("/postlike")
    public Result like(@RequestParam Integer postId, HttpServletRequest request) {
        return Result.success(newsService.toggleLike(UserContext.requireUserId(request), postId));
    }

    /** 查询点赞状态（无需登录，未登录视为未点赞） */
    @GetMapping("/postlike/status")
    public Result likeStatus(@RequestParam Integer postId, HttpServletRequest request) {
        return Result.success(newsService.getLikeStatus(UserContext.getUserId(request), postId));
    }
}