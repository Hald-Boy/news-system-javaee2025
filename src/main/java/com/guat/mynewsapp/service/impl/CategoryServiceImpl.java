package com.guat.mynewsapp.service.impl;

import com.guat.mynewsapp.entity.Category;
import com.guat.mynewsapp.mapper.CategoryMapper;
import com.guat.mynewsapp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加分类
     * @param category .
     */
    @Override
    public void addCategory(Category category) {
        categoryMapper.insertCategory(category);
    }



    /**
     * 修改分类
     * @param category .
     */
    @Override
    public void updateCategory(Category category) {
        categoryMapper.updateCategory(category);
    }



    /**
     * 删除分类
     * @param id .
     */
    @Override
    public void deleteCategory(Integer id) {
        categoryMapper.deleteCategory(id);
    }

    /**
     * 查询分类
     * @return .
     */
    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.getAllCategories();
    }


}