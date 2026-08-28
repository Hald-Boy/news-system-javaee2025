package com.guat.mynewsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类-项目的入口
 * 核心注解：@SpringBootApplication
 *    作用：开启自动配置、包扫描，运行 main 方法就能启动整个项目
 */
@SpringBootApplication
public class NewsCourseDesignApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsCourseDesignApplication.class, args);
    }

}
