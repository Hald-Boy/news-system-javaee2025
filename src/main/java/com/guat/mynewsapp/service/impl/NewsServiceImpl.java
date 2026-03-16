package com.guat.mynewsapp.service.impl;

import com.guat.mynewsapp.entity.*;
import com.guat.mynewsapp.mapper.CategoryMapper;
import com.guat.mynewsapp.mapper.NewsImageMapper;
import com.guat.mynewsapp.mapper.NewsMapper;
import com.guat.mynewsapp.mapper.UserMapper;
import com.guat.mynewsapp.service.NewsService;
import com.guat.mynewsapp.utils.FileUploadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class NewsServiceImpl implements NewsService {

//    @Autowired
//    private NewsMapper newsMapper;
//
//
//    @Value("${news.upload.path}")
//    private String uploadPath; //src/main/resources/uploads/news_images/
//
//    // 1. 提取文件路径常量（避免硬编码，课程设计级优化）
//    //private static final String IMAGE_BASE_PATH = "src/main/resources";
//    //private static final Logger log = LoggerFactory.getLogger(NewsServiceImpl.class);
//
//
//    /**
//     * 分页查询新闻列表，含条件查询
//     * @param page 第几页
//     * @param size 每页的记录数
//     * @return 返回的PageBean对象记录了数据列表和总记录数
//     */
//    @Override
//    public PageBean getAllNews(String titleKey, Integer categoryId, LocalDate begin, LocalDate end, Integer page, Integer size) {
//        //获取分页查询的起始索引
//        int start = (page - 1)*page;
//        PageBean pageBean = new PageBean();
//        //调用Mapper接口的方法
//        pageBean.setRows(newsMapper.selectNews(titleKey,categoryId,begin,end,start,size)); //新闻列表
//        pageBean.setTotal(newsMapper.countNews()); //总记录数
//        return pageBean;
//    }
//
//
//
//    /**
//     * 根据ID获取新闻详情
//     * @param id 要查看的新闻的id
//     * @return 返回一条新闻记录
//     */
//    @Override
//    public News getNewsById(Integer id) {
//        //获取新闻基本信息
//        News news = newsMapper.getNewsById(id);
//        if(news != null){
//            //获取关联图片
//            List<NewsImage> images = newsMapper.getImagesByNewsId(id);
//            news.setImages(images);
//
//            // 设置封面图为第一张图片（如果有）
//            if (!images.isEmpty()) {
//                news.setCoverImageUrl(images.get(0).getImageUrl());
//            }
//
//            //获取关联评论
//            List<Comment> comments = newsMapper.getCommentsByNewsId(id);
//            news.setComments(comments);
//        }
//        return news;
//    }
//
//
//
//    /**
//     * 发布新闻接口和文件上传接口合并成一个接口
//     * @param news 接收用户输入的新闻的基本信息
//     * @param images 接收用户上传的图片
//     */
//    @Override
//    public void publishNews(News news,MultipartFile[] images) {
//        // 1.先保存新闻到 news 表，获取新闻 ID
//        // 初始化发布时间、浏览量、点赞数
//        news.setCreateTime(LocalDateTime.now());
//        news.setViewCount(0);
//        news.setLikeCount(0);
//        newsMapper.insertNews(news); // insertNews通过设置会自动给ID赋值
//        int newsId = news.getId(); // 获取刚插入的新闻ID
//
//
//        if(images != null && images.length > 0){
//            List<NewsImage> newsImages = new ArrayList<>();
//
//
//            // 3. 遍历上传的图片，保存到新闻文件夹 + 记录到 news_image 表
//            for (MultipartFile image : images) {
//                if (image.isEmpty()) continue; // 跳过空文件
//
//                try {
//                    NewsImage newsImage = handleImageUpload(image, newsId);
//                    newsImages.add(newsImage); //向集合中添加元素
//
//                } catch (IOException e) {
//                    log.error("图片上传失败！",e);
//                    throw new RuntimeException("图片上传失败！",e);
//                }
//            }
//            // 3. 批量插入新闻图片（合并到NewsMapper）
//            if (!newsImages.isEmpty()) {
//                newsMapper.batchInsertNewsImages(newsImages);
//            }
//        }
//    }
//
//
//
//    /**
//     * 修改新闻
//     * @param news 用户修改的数据
//     */
//    @Override
//    @Transactional  //开启Spring事务管理
//    public void updateNews(News news, MultipartFile[] newImages,
//                           List<Integer> deletedImageIds,
//                           Map<Integer, Integer> imageSortOrders,
//                           List<Integer> newImageSortOrders
//    ) {
//
//        // 1. 更新新闻基本信息
//        newsMapper.updateNews(news);
//
//
//        // 2. 处理删除的图片
//        if (deletedImageIds != null && !deletedImageIds.isEmpty()) {
//            // 从数据库删除记录
//            newsMapper.deleteImagesByIds(deletedImageIds);
//
//            // 从文件系统删除物理文件
//            for (Integer imageId : deletedImageIds) {
//                //根据图片ID查询单张图片
//                NewsImage image = newsMapper.getImageById(imageId);
//                if (image != null) {
//                    String filePath = "src/main/resources" + image.getImageUrl();
//                    File file = new File(filePath);
//                    if (file.exists()) {
//                        file.delete();
//                    }
//                }
//            }
//        }
//
//
//        // 3. 处理新增的图片
//        if (newImages != null && newImages.length > 0) {
//            List<NewsImage> newsImages = new ArrayList<>();
//            Integer newsId = news.getId();
//
//
//            for (MultipartFile image : newImages) {
//                if (image.isEmpty()) continue;
//
//                try {
//                    NewsImage newsImage = handleImageUpload(image, newsId);
//                    newsImages.add(newsImage);
//                } catch (IOException e) {
//                    log.error("修改新闻时，图片 {} 上传失败", image.getOriginalFilename(), e);
//                    // 抛出自定义异常，触发事务回滚
//                    throw new BusinessException("图片上传失败，修改已回滚", e);
//                }
//            }
//
//
//            // 批量插入新图片
//            if (!newsImages.isEmpty()) {
//                newsMapper.batchInsertNewsImages(newsImages);
//            }
//        }
//
//
//        // 4. 更新图片排序（包括原图和新增图）
//        if (imageSortOrders != null && !imageSortOrders.isEmpty()) {
//            for (Map.Entry<Integer, Integer> entry : imageSortOrders.entrySet()) {
//                Integer imageId = entry.getKey();
//                Integer sortOrder = entry.getValue();
//                newsMapper.updateImageSortOrder(imageId, sortOrder);
//            }
//        }
//    }
//
//
//
//    /**
//     * 删除新闻
//     * @param newsId 新闻的id
//     */
//    @Override
//    @Transactional
//    public void deleteNews(Integer newsId) {
//        // 1. 查询新闻关联的图片,用于从文件系统删除图片
//        List<NewsImage> images = newsMapper.getImagesByNewsId(newsId);
//
//        // 2. 从文件系统删除图片
//        if (images != null) {
//            for (NewsImage image : images) {
//                String filePath = uploadPath + image.getImageUrl().substring("/uploads/".length());
//                log.info("从文件系统删除图片：filePath：{}", filePath);
//                File file = new File(filePath);
//                if (file.exists()) {
//                    file.delete();
//                }
//            }
//        }
//
//        // 3. 从数据库删除新闻图片记录
//        newsMapper.deleteImagesByNewsId(newsId);
//
//        // 4. 从数据库删除新闻评论记录（假设存在评论表）
//        newsMapper.deleteCommentsByNewsId(newsId);
//
//        // 5. 从数据库删除新闻主记录
//        newsMapper.deleteNewsById(newsId);
//    }
//
//
//
//
//
//    /**
//     * 文件上传工具方法
//     * @param image .
//     * @param newsId .
//     * @return .
//     * @throws IOException .
//     */
//    private NewsImage handleImageUpload(MultipartFile image, Integer newsId) throws IOException {
//        // 生成唯一文件名
//        String originalFilename = image.getOriginalFilename();
//        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String newFileName = UUID.randomUUID() + extension;
//
//        // 保存到新闻目录
//        String newsDir = uploadPath + File.separator + newsId;
//        File dir = new File(newsDir);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        File dest = new File(newsDir + File.separator + newFileName);
//        image.transferTo(dest); // 可能抛 IOException
//
//        // 构建 NewsImage
//        NewsImage newsImage = new NewsImage();
//        newsImage.setNewsId(newsId);
//        newsImage.setImageUrl("/uploads/news_images/" + newsId + "/" + newFileName);
//        newsImage.setSortOrder(0);
//        return newsImage;
//    }
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
        User user = userMapper.getUserById(news.getUserID());

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
            Category category = categoryMapper.getCategoryById(news.getCategoryID());
            User user = userMapper.getUserById(news.getUserID());

            news.setImages(images); // 将图片列表设置到News对象中
            news.setCategoryName(category.getName());
            news.setUserName(user.getUsername());
        }
        // 3. 查询总记录数
        Long total = newsMapper.selectTotal(title);
        // 4. 封装分页结果
        return new PageBean(total, rows);
    }

}