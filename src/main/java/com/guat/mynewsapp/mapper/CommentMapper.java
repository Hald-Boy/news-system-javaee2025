package com.guat.mynewsapp.mapper;

import com.guat.mynewsapp.entity.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {
    @Insert("insert into comment (news_id, user_id, content, create_time) values (#{newsID}, #{userID}, #{comment}, now())")
    void insertComment(Comment comment);
}
