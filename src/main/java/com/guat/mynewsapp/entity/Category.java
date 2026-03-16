package com.guat.mynewsapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private int id;     //分类ID
    private String name;    //分类名称
    private LocalDateTime createTime;   //创建时间
}
