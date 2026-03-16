package com.guat.mynewsapp.mapper;

import com.guat.mynewsapp.entity.NewsImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface NewsImageMapper {
    // 批量插入新闻图片
    int batchInsert(@Param("images") List<NewsImage> images);

    // 根据新闻ID查询图片
    List<NewsImage> selectByNewsId(Integer newsId);

    // 根据图片ID删除
    int deleteByIds(@Param("ids") List<Integer> ids);

    // 根据新闻ID删除图片
    int deleteByNewsId(Integer newsId);
}
