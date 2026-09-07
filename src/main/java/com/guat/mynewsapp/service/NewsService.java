package com.guat.mynewsapp.service;

import com.guat.mynewsapp.dto.PageBean;
import com.guat.mynewsapp.entity.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface NewsService {

// 新增新闻（含图片）
    void add(News news, MultipartFile[] newImages) throws IOException;

    // 修改新闻（含图片新增/删除）
    void update(News news, MultipartFile[] newImages, List<Integer> deleteImageIds) throws IOException;

    // 根据ID查询新闻（含图片）
    News getById(Integer id);

    // 删除新闻（含图片）
    void delete(Integer id) throws IOException;

    // 分页查询新闻
    PageBean pageQuery(String title, Integer pageNum, Integer pageSize);

    /** 点赞/取消点赞帖子，返回 {likeCount, isLiked} */
    Map<String, Object> toggleLike(Integer userId, Integer postId);

    /** 查询点赞状态，userId 为空时按未点赞处理，返回 {likeCount, isLiked} */
    Map<String, Object> getLikeStatus(Integer userId, Integer postId);

}