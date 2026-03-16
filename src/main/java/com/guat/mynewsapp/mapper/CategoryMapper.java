package com.guat.mynewsapp.mapper;

import com.guat.mynewsapp.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 添加分类
     * @param category .
     */
    @Insert("insert into category (name, create_time) values (#{name}, now())")
    void insertCategory(Category category);


    /**
     * 修改分类
     * @param category .
     */
    @Update("update category set name = #{name} where id = #{id}")
    void updateCategory(Category category);


    /**
     * 删除分类
     * @param id .
     */
    @Delete("delete from category where id = #{id}")
    void deleteCategory(Integer id);


//    /**
//     * 查询分类
//     * @return .
//     */
//    @Select("select * from category")
//    List<Category> getAllCategories();
// 修正查询语句 deepseek 2答
    @Select("select id, name, create_time as createTime from category")
    List<Category> getAllCategories();

    /**
     * 根据ID查询分类
     */
    @Select("select id,name,create_time from category where id = #{id}")
    Category getCategoryById(Integer id);

}