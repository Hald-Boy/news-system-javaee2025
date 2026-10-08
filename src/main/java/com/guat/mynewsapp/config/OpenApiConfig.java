package com.guat.mynewsapp.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * api接口文档配置
 */
@Configuration
@SecurityScheme(
        name = "BearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // 强制指定3.1.0版本，AI能识别
        return new OpenAPI()
                .openapi("3.1.0")
                .info(new Info()
                        .title("世界社区API文档")
                        .version("1.0.0")
                        .description("包含多模块接口"));
    }
}