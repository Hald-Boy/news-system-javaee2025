package com.guat.mynewsapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * api接口文档配置
 */
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // 强制指定3.1.0版本，AI能识别
        return new OpenAPI()
                .openapi("3.1.0")
                .info(new Info()
                        .title("新闻网API文档")
                        .version("1.0.0")
                        .description("包含登录/注册/用户/新闻接口"));
    }
}