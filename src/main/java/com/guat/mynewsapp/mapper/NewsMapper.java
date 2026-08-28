package com.guat.mynewsapp.mapper;

import com.guat.mynewsapp.entity.Comment;
import com.guat.mynewsapp.entity.News;
import com.guat.mynewsapp.entity.NewsImage;
import com.guat.mynewsapp.entity.NewsLike;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface NewsMapper {

    //2025/12/5   ------AI
    // 新增新闻（返回自增ID）
    int insert(News news);

    // 修改新闻
    int update(News news);

    // 删除新闻
    int delete(Integer id);

    // 根据ID查询新闻
    News selectById(Integer id);

    // 分页查询新闻（支持模糊标题查询）
    List<News> selectByPage(@Param("title") String title, @Param("start") Integer start, @Param("pageSize") Integer pageSize);

    // 分页查询总记录数
    Long selectTotal(@Param("title") String title);
}