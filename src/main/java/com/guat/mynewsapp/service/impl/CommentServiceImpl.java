package com.guat.mynewsapp.service.impl;

import com.guat.mynewsapp.entity.Comment;
import com.guat.mynewsapp.entity.PageBean;
import com.guat.mynewsapp.mapper.CommentMapper;
import com.guat.mynewsapp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 根据帖子ID去查询所有的一级评论
     *
     * @param id 帖子的ID
     * @return 封装一级评论和条数
     */
    @Override
    public PageBean getCommentByID(Integer id) {

        //获取所有一级评论
        List<Comment> rows = commentMapper.getCommentByNewsId(id);
        //获取一级评论的条数
        Long total = commentMapper.countRootComment(id) ;
        return new PageBean(total,rows);
    }



    /**
     * 根据帖子ID和父评论ID查找父评论的子评论
     *
     * @param newsId 帖子ID
     * @param parentId 父评论ID
     * @return 封装结果
     */
    @Override
    public PageBean getChildComment(Integer newsId, Integer parentId) {

        //根据newsId和parentId查询所有的子评论
        List<Comment> rows = commentMapper.getChildComment(newsId, parentId);
        //获取子评论的条数
        Long total = commentMapper.countChildComment(newsId, parentId);

        return new PageBean(total,rows);
    }



    /**新增评论
     *              ⚠⚠
     *             ⚠⚠⚠⚠
     *           ⚠⚠⚠⚠⚠⚠⚠⚠
     *         ⚠⚠⚠⚠⚠⚠⚠⚠⚠⚠⚠⚠
     *  ⚠⚠⚠⚠⚠⚠⚠ 逻辑较为复制 ⚠⚠⚠⚠⚠⚠⚠⚠
     *         ⚠⚠⚠⚠⚠⚠⚠⚠⚠⚠⚠⚠
     * @param comment 评论的信息（"帖子newsId", "父parentId", "回复谁toUserId", "内容comment")
     *                传入的参数有多种组合
     * @return 返回值是受影响的记录数，如果插入成功受影响条数为1，否则为0
     *          所以>0代表插入成功，反之则插入失败。
     */
    @Override
    public boolean addComment(Comment comment) {

        comment.setStatus(1); //设置评论有效
        comment.setCreateTime(LocalDateTime.now()); //设置评论的时间

        // 情况1：一级评论（不回复任何人）
        // 如果是一级评论，父评论为null，跳出if直接入库
        if (comment.getParentId() == null || comment.getParentId() == 0L) {
            comment.setParentId(0L);
            comment.setRootCommentId(null);
        } else {
            // 情况2：非一级评论，回复别人（楼中楼）
            // 拿到要回复的那条父评论
            Comment parentComment = commentMapper.selectById(comment.getParentId());
            // 父评论不存在，直接失败
            if (parentComment == null) {
                return false;
            }
            // 父是一级评论 → 当前评论的root就是父的id  （回复楼主）
            if (parentComment.getRootCommentId() == null) {
                comment.setRootCommentId(parentComment.getId());
            } else {
                // 父本身也是子评论 → 直接继承父已经存好的顶层rootId （回复其他楼的哥们）
                comment.setRootCommentId(parentComment.getRootCommentId());
            }
        }
        //操作数据库受影响的条数
        //1>0 返回true，0>0则返回false
        return commentMapper.insertComment(comment) > 0;

    }

    /**
     * @param id, userId
     * @return
     */
    @Override
    public boolean delComment(Long id, Long userId) {
        return  commentMapper.deleteComment(id, userId) > 0;
    }


}
