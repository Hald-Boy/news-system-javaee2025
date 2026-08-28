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