package com.guat.mynewsapp.service.impl;

import com.guat.mynewsapp.dto.MediaKeepDTO;
import com.guat.mynewsapp.dto.NewsEditDTO;
import com.guat.mynewsapp.dto.PageBean;
import com.guat.mynewsapp.entity.*;
import com.guat.mynewsapp.exception.BusinessException;
import com.guat.mynewsapp.mapper.*;
import com.guat.mynewsapp.service.NewsService;
import com.guat.mynewsapp.utils.FileUploadUtil;
import com.guat.mynewsapp.utils.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
    private UserMapper userMapper;

    @Autowired
    private PostLikeMapper  postLikeMapper;

    @Autowired
    private NewsImageMapper imageMapper;


    /**
     * 新增新闻
     * @param news .
     * @param newImages .
     * @throws IOException .
     */
    @Transactional(rollbackFor = Exception.class) // 任意异常都回滚
    @Override
    public void add(News news, MultipartFile[] newImages) throws IOException {
        // 0. 计算内容类型：有图=图文(2)，无图=纯文字(1)；视频(3)/混合(4) 待视频上传功能支持
        news.setMediaType(newImages != null && newImages.length > 0 ? 2 : 1);
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
                // 2026/9/17 9:53 目前还没有开发图片和混合的情况，这里简单控制一下media_type为图片，1图片，2视频，3混合
                newsImage.setMediaId(1);
                imageList.add(newsImage);
            }
            // 批量插入图片
            if (!imageList.isEmpty()) {
                newsImageMapper.batchInsert(imageList);
            }
        }
    }




    /**
     * 修改新闻：支持标题/内容编辑、新增图片、删除图片、拖拽排序
     * 参数约定（keepMediaList / newMediaSortList 均为 JSON 字符串，由 Controller 解析）：
     *  - keepMediaList：前端编辑后【要保留】的旧图片列表，元素含 id 和拖拽后的新 sortOrder
     *      传 null → 不动旧图片；传 [] → 删除全部旧图片；只列部分 → 未列出的旧图片被删除
     *  - newMediaSortList：与 newImages 文件一一对应的排序号数组，如 [3,4]
     *      不传时新图片自动追加到已有图片之后
     */
    @Transactional(rollbackFor = Exception.class) // 任意异常都回滚
    @Override
    public void update(Integer id, NewsEditDTO newsEditDTO, MultipartFile[] newImages, List<MediaKeepDTO> keepMediaList, List<Integer> newMediaSortList, Integer loginUserId, Integer loginUserRole) throws IOException {
        //1. 查询原有帖子
        News oldNews = newsMapper.selectById(id);
        if (oldNews == null || "1".equals(oldNews.getIsDeleted())) {
            throw new IllegalArgumentException("帖子不存在或已删除");
        }

        // 权限校验：本人 or 管理员
        boolean isAuthor = oldNews.getUserId().equals(loginUserId);
        boolean isAdmin = 1 == loginUserRole;
        if (!isAuthor && !isAdmin) {
            throw new IllegalArgumentException("你无权限编辑该帖子");
        }

        //2. 更新帖子标题、内容、更新时间
        oldNews.setTitle(newsEditDTO.getTitle());
        oldNews.setContent(newsEditDTO.getContent());
        oldNews.setUpdateTime(LocalDateTime.now());
        newsMapper.update(oldNews);

        //3. 处理旧图片（删除 + 拖拽排序）
        List<NewsImage> dbImageList = imageMapper.selectByNewsId(id);
        // keepMediaList == null 表示前端未操作图片 → 全部保留；空列表 → 全部删除
        if (keepMediaList != null) {
            // 需要保留的图片id集合（统一转 Long 比较，避免 Integer/Long equals 永远 false 的坑）
            List<Long> keepMediaIds = keepMediaList.stream().map(MediaKeepDTO::getId).toList();

            // 3.1 删除不在保留列表中的图片：软删（标记 is_deleted 为 1，保留数据与本地文件）
            List<Integer> deleteImageIds = new ArrayList<>();
            for (NewsImage dbImage : dbImageList) {
                if (!keepMediaIds.contains(dbImage.getId().longValue())) {
                    deleteImageIds.add(dbImage.getId());
                }
            }
            if (!deleteImageIds.isEmpty()) {
                imageMapper.logicalDeleteByIds(deleteImageIds);
            }

            // 3.2 保留的图片：按前端传的新 sortOrder 更新（拖拽换位）
            for (MediaKeepDTO keep : keepMediaList) {
                NewsImage updateImage = new NewsImage();
                updateImage.setId(keep.getId().intValue());
                updateImage.setSortOrder(keep.getSortOrder());
                imageMapper.updateById(updateImage);
            }
        }

        //4. 处理本次新上传图片：newMediaSortList 与 newImages 文件一一对应，指定各自排序号
        if (newImages != null && newImages.length > 0) {
            // 兜底排序基准：优先取保留列表的最大排序号；没传保留列表就用数据库当前最大排序号
            int baseSort;
            if (keepMediaList != null && !keepMediaList.isEmpty()) {
                baseSort = keepMediaList.stream().mapToInt(MediaKeepDTO::getSortOrder).max().orElse(0);
            } else {
                baseSort = dbImageList.stream().mapToInt(NewsImage::getSortOrder).max().orElse(0);
            }

            List<NewsImage> imageList = new ArrayList<>();
            for (int i = 0; i < newImages.length; i++) {
                MultipartFile file = newImages[i];
                if (file.isEmpty()) {
                    continue;
                }
                // 上传图片获取URL
                String imageUrl = fileUploadUtil.upload(file);
                // 封装图片实体：排序号优先取前端指定，否则追加到末尾
                NewsImage newsImage = new NewsImage();
                newsImage.setNewsId(id);
                newsImage.setImageUrl(imageUrl);
                newsImage.setMediaId(1); // 目前只支持图片，media_type=1
                Integer sortOrder = (newMediaSortList != null && i < newMediaSortList.size() && newMediaSortList.get(i) != null)
                        ? newMediaSortList.get(i)
                        : baseSort + i + 1;
                newsImage.setSortOrder(sortOrder);
                imageList.add(newsImage);
            }
            // 批量插入图片
            if (!imageList.isEmpty()) {
                imageMapper.batchInsert(imageList);
            }
        }

        //5. 重新统计剩余有效图片，刷新帖子的 mediaType（1纯文字 2图文）
        refreshMediaInfo(id);
    }


    /**
     * 根据帖子当前剩余的有效图片数刷新 media_type：有图=2(图文)，无图=1(纯文字)
     */
    private void refreshMediaInfo(Integer newsId) {
        List<NewsImage> images = imageMapper.selectByNewsId(newsId);
        News updateNews = new News();
        updateNews.setId(newsId);
        updateNews.setMediaType(images.isEmpty() ? 1 : 2);
        newsMapper.update(updateNews);
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
        news.setAvatar(user.getAvatar());
        news.setUserAccount(user.getUserAccount());
        news.setImages(images);
        return news;
    }


    /**
     *
     * @param id 帖子id
     * @param loginUserId 当前登录的用户id
     * @param loginUserRole 当前登录用户的身份
     * @throws IOException ..
     */
    @Transactional(rollbackFor = Exception.class) // 任意异常都回滚
    @Override
    public void delete(Integer id, Integer loginUserId, Integer loginUserRole) throws IOException {

        // 1. 先查询这条帖子
        News news = newsMapper.selectById(id);
        // 帖子不存在 或者 已经被逻辑删除
        if (news == null || "1".equals(news.getIsDeleted())) {
            throw new BusinessException("帖子不存在或已删除");
        }

        // 2. 权限判断：管理员  OR 帖子的发布者本人
        boolean isAdmin = 1 == loginUserRole; //假设role=1代表管理员，你按自己字段改
        boolean isAuthor = loginUserId.equals(news.getUserId());

        if (!isAdmin && !isAuthor) {
            throw new BusinessException("无权限删除该帖子，只能删除自己发布的内容");
        }
        // 逻辑删除：仅标记 is_deleted='1'，保留数据与文件（全站查询均按 is_deleted 过滤）
        newsMapper.logicalDelete(id);
    }




    /**
     * 多条件分页查询新闻
     * @param title .
     * @param pageNum .
     * @param pageSize .
     * @return .
     */
    @Override
    public PageBean<News> pageQuery(String title, Integer pageNum, Integer pageSize) {
        // 1. 计算分页起始位置
        int start = (pageNum - 1) * pageSize;
        // 2. 查询分页数据
        List<News> list = newsMapper.selectByPage(title, start, pageSize);
        // 遍历新闻，关联查询对应的图片和作者
        for (News news : list) {
            List<NewsImage> images = newsImageMapper.selectByNewsId(news.getId());
            User user = userMapper.getUserById(news.getUserId());

            news.setImages(images); // 将图片列表设置到News对象中
            news.setUserName(user.getUsername());
            news.setAvatar(user.getAvatar());
            news.setUserAccount(user.getUserAccount());
        }
        // 3. 查询总记录数
        Long total = newsMapper.selectTotal(title);
        // 4. 封装分页结果
        return new PageBean<>(list,total,pageNum,pageSize);
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
        // 三处冗余计数保持一致：news_like 已落库，这里同步 news 与作者 user
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
