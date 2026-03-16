package com.guat.mynewsapp.mapper;

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

//    /**
//     * 分页查询（包含条件查询：根据标题的模糊查询，根据分类id的查询，根据创建时间的查询）
//     * @return 返回查询总记录数，下面的是返回员工信息的集合
//     */
//    @Select("select count(*) from news")
//    Long countNews();
//    List<News> selectNews(String titleKey, Integer categoryId, LocalDate begin, LocalDate end, int start, int size);
//
//
//
//    /**
//     * 根据新闻ID查询新闻详情
//     * 先查询新闻的基本信息：id，标题，内容，分类id，作者id，创建时间，浏览量，点赞数，，，再查询和新闻id关联的图片、评论
//     * @param id 新闻的id
//     * @return 返回新闻基本信息、图片信息，评论信息
//     */
//    News getNewsById(Integer id);
//    //关联id的图片列表，参数id是新闻id
//    List<NewsImage> getImagesByNewsId(Integer id);
//    //关联id的评论列表，参数id是新闻id
//    List<Comment> getCommentsByNewsId(Integer id);
//
//
//
//    /**
//     * 发布新闻
//     * 插入新闻基本信息和图片
//     */
//    void insertNews(News news);
//    // 批量插入新闻图片
//    void batchInsertNewsImages(@Param("images") List<NewsImage> images);
//
//
//
//    /**
//     * 修改新闻
//     * @param news 用户输入的值
//     */
//    void updateNews(News news);
//    //修改新闻时删除的图片
//    void deleteImagesByIds(List<Integer> deletedImageIds);
//    //调整顺序：用户拖拽调整图片顺序后，生成imageSortOrders映射
//    void updateImageSortOrder(Integer imageId, Integer sortOrder);
//    //根据id获取图片
//    NewsImage getImageById(Integer imageId);
//
//
//
//    /**
//     * 删除新闻
//     * @param newsId 新闻id
//     */
//    //从数据库删除新闻图片记录
//    void deleteImagesByNewsId(Integer newsId);
//    //从数据库删除新闻评论记录
//    void deleteCommentsByNewsId(Integer newsId);
//    //从数据库删除新闻主记录
//    void deleteNewsById(Integer newsId);


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
}