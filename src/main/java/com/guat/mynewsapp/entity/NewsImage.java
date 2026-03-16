package com.guat.mynewsapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsImage {
    private Integer id;         // 图片ID
    private Integer newsId;     // 关联新闻ID
    private String imageUrl;    // 图片URL
    private Integer sortOrder;  // 排序序号
    private LocalDateTime createTime; // 创建时间
}