package com.guat.mynewsapp.mapper;

import com.guat.mynewsapp.entity.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface ReportMapper {

    Report findByUserAndTarget(@Param("userId") Long userId,
                               @Param("reportType") Integer reportType,
                               @Param("targetId") Long targetId);

    int insert(Report report);
}
