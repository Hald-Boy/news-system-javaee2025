package com.guat.mynewsapp.service;

import com.guat.mynewsapp.entity.Category;
import java.util.List;

public interface CategoryService {
    //添加分类
    void addCategory(Category category);
    //修改分类
    void updateCategory(Category category);
    //删除分类
    void deleteCategory(Integer id);
    //查询分类
    List<Category> getAllCategories();
}
