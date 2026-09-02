package com.guat.mynewsapp.mapper;

import com.guat.mynewsapp.entity.CommentLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface CommentLikeMapper {

    CommentLike findByUserAndComment(@Param("userId") Long userId, @Param("commentId") Long commentId);

    int insert(CommentLike commentLike);

    int updateCancel(@Param("id") Long id, @Param("isCancel") Integer isCancel);
}
