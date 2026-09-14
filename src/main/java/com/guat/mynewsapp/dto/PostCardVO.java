package com.guat.mynewsapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 作品列表项（帖子卡片）
 */
@Data
@NoArgsConstructor
public class PostCardVO {

    private Integer id;
    private String title;
    private String content;
    private String userName;
    private Integer likeCount;
    private Integer commentCount;
    private Integer collectCount;
    /** 首图 URL（无图则 null） */
    private String cover;
    private LocalDateTime createTime;
}
