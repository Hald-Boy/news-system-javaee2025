package com.guat.mynewsapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int id;     //评论ID
    private int newsID;     //评论的新闻的ID---》关联新闻表news的id
    private int userID;     //评论者的ID---》关联用户表user的id
    private String comment;     //评论的内容
    private LocalDate createDate;   //评论的时间
}
