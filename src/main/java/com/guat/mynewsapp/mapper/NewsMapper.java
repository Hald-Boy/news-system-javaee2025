package com.guat.mynewsapp.mapper;

import com.guat.mynewsapp.dto.PostCardVO;
import com.guat.mynewsapp.entity.Comment;
import com.guat.mynewsapp.entity.News;
import com.guat.mynewsapp.entity.NewsImage;
import com.guat.mynewsapp.entity.NewsLike;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface NewsMapper {

    //2025/12/5   ------AI
    // 新增新闻（返回自增ID）
    int insert(News news);

    // 修改新闻
    int update(News news);

    // 删除新闻
    int delete(Integer id);

    // 根据ID查询新闻
    News selectById(Integer id);

    // 分页查询新闻（支持模糊标题查询）
    List<News> selectByPage(@Param("title") String title, @Param("start") Integer start, @Param("pageSize") Integer pageSize);

    // 分页查询总记录数
    Long selectTotal(@Param("title") String title);

    // 根据id查询帖子，和上面的 selectById() 是一样的功能，后续优化记得只留其一
    News findById(@Param("id") Integer id);

    /** 增减帖子点赞数，delta 可为 ±1 */
    int updateLikeCount(@Param("id") Integer id, @Param("delta") int delta);

    /** 某用户的帖子数（我的作品） */
    int countByUserId(@Param("userId") Integer userId);

    /** 某用户的帖子分页列表（我的作品，带首图） */
    List<PostCardVO> listByUserId(@Param("userId") Integer userId,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);
}