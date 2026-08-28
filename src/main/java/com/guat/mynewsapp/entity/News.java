package com.guat.mynewsapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class News {
    private Integer id;     //新闻id
    private String title;   //新闻标题
    private String content;     //新闻内容
    private Integer categoryID;     //分类ID
    private String categoryName; //分类名称
    private Integer userID;     //作者ID
    private String userName;//作者名称
    private LocalDateTime createTime;   //新闻发布时间
    private String coverImageUrl; //封面图片 URL，，，，，，，，，，，，，图片表 关联新闻的第一张图片
    private List<NewsImage> images;    //图片列表

}
