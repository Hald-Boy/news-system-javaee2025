package com.guat.mynewsapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论收藏列表项
 */
@Data
@NoArgsConstructor
public class CommentCardVO {

    private Long id;
    private String username;
    private String content;
    private Integer likeCount;
    private Long newsId;
    /** 所属帖子标题 */
    private String postTitle;
    private LocalDateTime createTime;
}
