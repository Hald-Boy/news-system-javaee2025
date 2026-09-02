package com.guat.mynewsapp.mapper;

import com.guat.mynewsapp.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostLikeMapper {

    //根据用户id和帖子id查询所有点赞记录
    PostLike findByUserAndPost(@Param("userId") Long userId, @Param("postId") Long postId);

    //插入新闻点赞记录
    int insert(PostLike postLike);

    //设置点赞记录是否有效
    int updateCancel(@Param("id") Long id, @Param("isCancel") Integer isCancel);
}
