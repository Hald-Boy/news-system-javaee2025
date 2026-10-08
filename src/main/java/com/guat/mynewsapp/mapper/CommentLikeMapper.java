package com.guat.mynewsapp.mapper;

import com.guat.mynewsapp.entity.CommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface CommentLikeMapper {

    //根据用户id和评论id查询是否已存在点赞记录
    CommentLike findByUserAndComment(@Param("userId") Long userId, @Param("commentId") Long commentId);

    int insert(CommentLike commentLike);

    int updateCancel(@Param("id") Long id, @Param("isCancel") Integer isCancel);
}
