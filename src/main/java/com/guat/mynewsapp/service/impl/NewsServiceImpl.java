package com.guat.mynewsapp.service.impl;

import com.guat.mynewsapp.entity.*;
import com.guat.mynewsapp.exception.BusinessException;
import com.guat.mynewsapp.mapper.*;
import com.guat.mynewsapp.service.NewsService;
import com.guat.mynewsapp.utils.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class NewsServiceImpl implements NewsService {


    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private NewsImageMapper newsImageMapper;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostLikeMapper  postLikeMapper;


    /**
     * 新增新闻
     * @param news .
     * @param newImages .
     * @throws IOException .
     */
    @Transactional(rollbackFor = Exception.class) // 任意异常都回滚
    @Override
    public void add(News news, MultipartFile[] newImages) throws IOException {
        // 1. 新增新闻主表
        newsMapper.insert(news);
        Integer newsId = news.getId(); // 新增新闻后，获取自增ID

        // 2. 处理图片上传（如果有图片）
        if (newImages != null && newImages.length > 0) {

            List<NewsImage> imageList = new ArrayList<>();

            for (int i = 0; i < newImages.length; i++) {
                MultipartFile file = newImages[i];
                if (file.isEmpty()) {
                    continue;
                }
                // 上传图片获取URL
                String imageUrl = fileUploadUtil.upload(file);
                // 封装图片实体（排序号=索引+1）
                NewsImage newsImage = new NewsImage();
                newsImage.setNewsId(newsId);
                newsImage.setImageUrl(imageUrl);
                newsImage.setSortOrder(i + 1);
                imageList.add(newsImage);
            }
            // 批量插入图片
            if (!imageList.isEmpty()) {
                newsImageMapper.batchInsert(imageList);
            }
        }
    }




    /**
     * 修改新闻，删除服务器本地文件和数据库url
     * @param news .
     * @param newImages .
     * @param deleteImageIds .
     * @throws IOException .
     */
    @Transactional(rollbackFor = Exception.class) // 任意异常都回滚
    @Override
    public void update(News news, MultipartFile[] newImages, List<Integer> deleteImageIds) throws IOException {
        // 1. 更新新闻主表
        newsMapper.update(news);
        Integer newsId = news.getId();

        // 2. 处理图片删除（如果有要删除的图片ID）
        if (deleteImageIds != null && !deleteImageIds.isEmpty()) {
            // 查询要删除的图片信息（获取URL用于删文件）
            List<NewsImage> deleteImages = newsImageMapper.selectByNewsId(newsId);
            deleteImages.stream()
                    .filter(img -> {
                        // 新增这行：打印ID值+类型
                        System.out.println("图片ID：" + img.getId() + "，类型：" + img.getId().getClass().getName());
                        System.out.println("deleteImageIds中的元素类型：" + deleteImageIds.get(0).getClass().getName());
                        return deleteImageIds.contains(img.getId());
                    })
                    .forEach(img -> fileUploadUtil.deleteFile(img.getImageUrl()));
            // 删除数据库中的图片记录
            newsImageMapper.deleteByIds(deleteImageIds);
        }

        // 3. 处理新增图片（如果有）
        if (newImages != null && newImages.length > 0) {
            // 根据新闻ID查询当前最大排序号
            List<NewsImage> existImages = newsImageMapper.selectByNewsId(newsId);
            int maxSort = existImages.stream().mapToInt(NewsImage::getSortOrder).max().orElse(0);

            List<NewsImage> imageList = new ArrayList<>();
            for (int i = 0; i < newImages.length; i++) {
                MultipartFile file = newImages[i];
                if (file.isEmpty()) {
                    continue;
                }
                //保存图片到服务器本地，返回文件的URL保存到数据库
                String imageUrl = fileUploadUtil.upload(file);

                NewsImage newsImage = new NewsImage();
                newsImage.setNewsId(newsId);
                newsImage.setImageUrl(imageUrl);
                newsImage.setSortOrder(maxSort + i + 1); // 排序号递增
                imageList.add(newsImage);
            }
            //保存url到数据库
            if (!imageList.isEmpty()) {
                newsImageMapper.batchInsert(imageList);
            }
        }
    }




    /**
     * 根据id查询新闻/查看新闻详情/查询回显
     * @param id .
     * @return .
     */
    @Override
    public News getById(Integer id) {
        // 1. 查询新闻主信息
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new RuntimeException("新闻不存在！");
        }
        // 2. 查询关联图片
        List<NewsImage> images = newsImageMapper.selectByNewsId(id);

        //3.查询新闻的作者
        User user = userMapper.getUserById(news.getUserId());

        news.setUserName(user.getUsername());
        news.setImages(images);
        return news;
    }




    /**
     * 删除新闻
     * @param id .
     * @throws IOException .
     */
    @Transactional(rollbackFor = Exception.class) // 任意异常都回滚
    @Override
    public void delete(Integer id) throws IOException {
        // 1. 查询关联图片（用于删文件）
        List<NewsImage> images = newsImageMapper.selectByNewsId(id);
        // 4. 删除图片文件
        for (NewsImage img : images) {
            fileUploadUtil.deleteFile(img.getImageUrl());
        }
        // 3. 删除图片关联表
        newsImageMapper.deleteByNewsId(id);
        // 2. 删除新闻主表
        newsMapper.delete(id);
    }




    /**
     * 多条件分页查询新闻
     * @param title .
     * @param pageNum .
     * @param pageSize .
     * @return .
     */
    @Override
    public PageBean pageQuery(String title, Integer pageNum, Integer pageSize) {
        // 1. 计算分页起始位置
        int start = (pageNum - 1) * pageSize;
        // 2. 查询分页数据
        List<News> rows = newsMapper.selectByPage(title, start, pageSize);
        // 遍历新闻，关联查询对应的图片
        for (News news : rows) {
            List<NewsImage> images = newsImageMapper.selectByNewsId(news.getId());
            Category category = categoryMapper.getCategoryById(news.getCategoryId());
            User user = userMapper.getUserById(news.getUserId());

            news.setImages(images); // 将图片列表设置到News对象中
            news.setCategoryName(category.getName());
            news.setUserName(user.getUsername());
        }
        // 3. 查询总记录数
        Long total = newsMapper.selectTotal(title);
        // 4. 封装分页结果
        return new PageBean(total, rows);
    }


    /**
     * 点赞 / 取消点赞（需登录）
     * 场景：点击 ❤ 时调用
     *
     * @param userId 当前登录的用户的id
     * @param postId 要点赞的帖子的id
     * @return 把帖子当前的点赞数和用户行为 点赞/取消点赞 1/0 结果封装返回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> toggleLike(Integer userId, Integer postId) {
        News post = newsMapper.findById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        int delta;
        // 传入用户id和帖子id查询是否已经存在点赞记录
        PostLike record = postLikeMapper.findByUserAndPost(userId.longValue(), postId.longValue());
        if (record == null) {
            //如果还未点赞，则 ↓
            PostLike pl = new PostLike();
            pl.setUserId(userId.longValue());
            pl.setPostId(postId.longValue());
            pl.setIsCancel(0); // 0有效点赞 1无效点赞
            // 插入点赞记录
            postLikeMapper.insert(pl);
            delta = 1;
        } else if (Integer.valueOf(1).equals(record.getIsCancel())) {
            // 之前点赞但已取消 -> 重新点赞
            // 传入之前点赞的那条记录的id和设置为有效点赞
            postLikeMapper.updateCancel(record.getId(), 0);
            delta = 1;
        } else {
            // 已点赞 -> 取消
            postLikeMapper.updateCancel(record.getId(), 1);
            delta = -1;
        }
        // 三处冗余计数保持一致：post_like 已落库，这里同步 news 与作者 user
        // 增加/减少帖子点赞数
        newsMapper.updateLikeCount(postId, delta);
        // 增加/减少获赞总数，参数是这条帖子作者的id和 ±1
        userMapper.updateTotalLikeCount(post.getUserId(), delta);

        // 增加/减少帖子点赞之后获取当前帖子的点赞数
        // int likeCount = (post.getLikeCount() == null ? 0 : post.getLikeCount()) + delta; 并发很高时可能用旧值计算，下面重新查询数据库较为稳妥
        News newPost = newsMapper.findById(postId);
        int likeCount = newPost.getLikeCount() == null ? 0 : newPost.getLikeCount();
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
     * 查询点赞状态（无需登录，未登录视为未点赞）
     * 加载页面时调用，你刷抖音点开一个视频，还没有点红心，页面一加载，红心就已经知道是红色还是空心，不是你点击之后才知道的。
     *
     *
     * @param userId 用户id，可为 null
     * @param postId 帖子 id
     * @return 封装结果返回
     */
    @Override
    public Map<String, Object> getLikeStatus(Integer userId, Integer postId) {
        News post = newsMapper.findById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        boolean isLiked = false;
        if (userId != null) {
            // 传入用户id和帖子id查询是否已经存在点赞记录
            PostLike record = postLikeMapper.findByUserAndPost(userId.longValue(), postId.longValue());
            // 如果查询到有点赞记录并且还是有效点赞（0），lsLiked就为true
            isLiked = record != null && Integer.valueOf(0).equals(record.getIsCancel());
        }
        Map<String, Object> result = new HashMap<>();
        //获取当前帖子的点赞数，和 true/false给前端是否渲染红心
        result.put("likeCount", post.getLikeCount() == null ? 0 : post.getLikeCount());
        result.put("isLiked", isLiked);
        return result;
    }

}