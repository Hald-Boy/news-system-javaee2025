package com.guat.mynewsapp.service;

import com.guat.mynewsapp.entity.Comment;
import com.guat.mynewsapp.entity.PageBean;

public interface CommentService {

    //查询一级评论
    PageBean getCommentByID(Integer id);

    //查询子评论
    PageBean getChildComment(Integer newsId, Integer parentId);

    //发表评论
    boolean addComment(Comment comment);

    //删除评论
    boolean delComment(Long id, Long userId);

}
