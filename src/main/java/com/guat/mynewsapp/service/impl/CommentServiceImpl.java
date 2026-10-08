package com.guat.mynewsapp.service.impl;

import com.guat.mynewsapp.entity.Comment;
import com.guat.mynewsapp.entity.CommentLike;
import com.guat.mynewsapp.dto.PageBean;
import com.guat.mynewsapp.exception.BusinessException;
import com.guat.mynewsapp.mapper.CommentDislikeMapper;
import com.guat.mynewsapp.mapper.CommentLikeMapper;
import com.guat.mynewsapp.mapper.CommentMapper;
import com.guat.mynewsapp.mapper.NewsMapper;
import com.guat.mynewsapp.service.CommentService;
import com.guat.mynewsapp.entity.CommentDislike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Autowired
    private CommentDislikeMapper commentDislikeMapper;

    @Autowired
    private NewsMapper newsMapper;

    /**
     * 根据帖子ID去查询所有的一级评论
     *
     * @param id 帖子的ID
     * @param currentUserId 当前登录用户ID（可为 null，游客不附带点赞状态）
     * @return 封装一级评论和条数
     */
    @Override
    public PageBean<Comment> getCommentByID(Integer id, int pageNum, int pageSize, Integer currentUserId) {

        if(pageNum < 1) pageNum = 1;
        int page = (pageNum - 1) * pageSize;

        //获取所有一级评论
        List<Comment> list = commentMapper.getCommentByNewsId(id,page,pageSize,currentUserId);
        //获取一级评论的条数
        Long total = commentMapper.countRootComment(id) ;
        long safeTotal = total == null ? 0 : total;
        return new PageBean<>(list,safeTotal,pageNum,pageSize);
    }



    /**
     * 根据帖子ID和父评论ID查找父评论的子评论
     *
     * @param newsId 帖子ID
     * @param parentId 父评论ID
     * @param currentUserId 当前登录用户ID（可为 null，游客不附带点赞状态）
     * @return 封装结果
     */
    @Override
    public PageBean<Comment> getChildComment(Integer newsId, Integer parentId,int pageNum, int pageSize, Integer currentUserId) {

        if(pageNum < 1) pageNum = 1;
        int page = (pageNum - 1) * pageSize;

        //根据newsId和parentId查询所有的子评论
        List<Comment> list = commentMapper.getChildComment(newsId, parentId,page,pageSize,currentUserId);
        //获取子评论的条数
        Long total = commentMapper.countChildComment(newsId, parentId);
        long safeTotal = total == null ? 0 : total;

        return new PageBean<>(list,safeTotal,pageNum,pageSize);
    }



    /**新增评论
     * @param comment 评论的信息（"帖子newsId", "父parentId", "回复谁toUserId", "内容comment")
     *                传入的参数有多种组合
     * @return 返回值是受影响的记录数，如果插入成功受影响条数为1，否则为0
     *          所以>0代表插入成功，反之则插入失败。
     */
    @Transactional(rollbackFor = Exception.class) // 加上事务
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
                // 情况3：父本身也是子评论 → 直接继承父已经存好的顶层rootId （回复其他楼的哥们）
                comment.setRootCommentId(parentComment.getRootCommentId());
            }
        }
        int total =  commentMapper.insertComment(comment);
        // 评论数+1
        newsMapper.updateCommentCount(comment.getNewsId(),1);
        //操作数据库受影响的条数
        //返回true或返回false
        return total > 0;
    }

    /**
     * 删除评论
     * @param id, userId
     * @return 删除成功返回真，反之假
     */
    @Transactional(rollbackFor = Exception.class) // 加上事务
    @Override
    public boolean delComment(Long id, Long userId) {
        Comment comment = commentMapper.selectById(id);
        int total = commentMapper.deleteComment(id, userId);
        // 评论数-1
        newsMapper.updateCommentCount(comment.getNewsId(),-1);
        return  total > 0;
    }



    /**
     * 评论点赞/取消点赞功能，调用的是CommentLikeMapper.java
     * @param userId 传入用户id
     * @param commentId 传入要点赞的评论id
     * @return 把评论当前的点赞数和用户行为 点赞/取消点赞 1/0 结果封装返回
     * 成功返回格式：isLiked为true代表是点赞操作/为false代表是取消点赞操作；likeCount代表当前评论的点赞总数
     * {
     * 	"code": 200,
     * 	"msg": "success",
     * 	"data": {
     * 		"isLiked": false,
     * 		"likeCount": 0
     * 	    }
     * }
     * 失败返回格式：
     * {
     * 	"code": 400,
     * 	"msg": "评论不存在",
     * 	"data": null
     * }
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> toggleLike(Integer userId, Long commentId) {
        // 先查询评论是否存在
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        // delta 1点赞 -1取消点赞
        int delta;
        // 传入用户id和帖子id查询是否已经存在点赞记录
        CommentLike record = commentLikeMapper.findByUserAndComment(userId.longValue(), commentId);
        if (record == null) {
            // 如果还未点赞，则 ↓
            CommentLike cl = new CommentLike();
            cl.setUserId(userId.longValue());
            cl.setCommentId(commentId);
            cl.setIsCancel(0);
            // 插入点赞记录
            commentLikeMapper.insert(cl);
            delta = 1;
        } else if (Integer.valueOf(1).equals(record.getIsCancel())) {
            // getIsCancel() 0为有效点赞，1为无效点赞/已取消点赞
            // 之前点赞但已取消 -> 重新点赞
            // 传入之前点赞的那条记录的id并设置为有效点赞
            commentLikeMapper.updateCancel(record.getId(), 0);
            delta = 1;
        } else {
            //点赞记录不为空，也是有效点赞
            commentLikeMapper.updateCancel(record.getId(), 1);
            delta = -1;
        }
        //增加/减少评论点赞数
        commentMapper.updateLikeCount(commentId, delta);
        //获取评论当前的获赞总数封装数据返回给前端
        //int likeCount = (comment.getLikeCount() == null ? 0 : comment.getLikeCount()) + delta;    并发很高时可能用旧值计算，下面重新查询数据库较为稳妥
        Comment comment1 = commentMapper.selectById(commentId);
        int likeCount = (comment1.getLikeCount() == null ? 0 : comment1.getLikeCount());
        // 封装结果
        Map<String, Object> result = new HashMap<>();
        //防止极端情况点赞数变成负数。
        //比如 bug 导致多次取消点赞，`likeCount=-1`，强制变成 0，前端不会出现 `-1` 点赞。
        result.put("likeCount", Math.max(likeCount, 0));
        //如果delta > 0 → true，如果delta < 0 → false
        result.put("isLiked", delta > 0);
        return result;
    }

    /**
     * 评论折叠功能，调用的是CommentDislikeMapper.java
     * 用户点击“踩”时调用调用
     * @param userId 当前登录的用户id
     * @param commentId 评论的id
     * @return 给前端返回true/false决定是否渲染折叠评论
     */
    @Override
    public Map<String, Object> toggleDislike(Integer userId, Long commentId) {
        // 根据id查询评论
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        boolean isDisliked;
        // 根据用户id和评论id查询“踩”的记录
        CommentDislike record = commentDislikeMapper.findByUserAndComment(userId.longValue(), commentId);
        if (record == null) {
            // 如果未查询到已存在“踩”记录
            CommentDislike cd = new CommentDislike();
            cd.setUserId(userId.longValue());
            cd.setCommentId(commentId);
            cd.setIsCancel(0);
            // 插入“踩”记录
            commentDislikeMapper.insert(cd);
            isDisliked = true;
        } else if (Integer.valueOf(1).equals(record.getIsCancel())) {
            // 如果存在“踩”记录，并且已经取消了“踩”
            // 更新“踩”的状态为有效 0有效 1无效
            commentDislikeMapper.updateCancel(record.getId(), 0);
            isDisliked = true;
        } else {
            // 如果如果存在“踩”记录，并且是有效的踩记录
            commentDislikeMapper.updateCancel(record.getId(), 1);
            isDisliked = false;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("isDisliked", isDisliked);
        return result;
    }

    /**
     * 查询当前用户已折叠的评论 id 集合（评论列表初始化时用）
     * @param userId 当前登录用户的id
     * @param commentIds 某帖子的一页评论id合集
     * @return 返回折叠评论id合集
     */
    @Override
    public List<Long> getFoldedCommentIds(Long userId, List<Long> commentIds) {
        if (userId == null || commentIds == null || commentIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return commentDislikeMapper.findFoldedCommentIds(userId, commentIds);
    }
}
