package com.audiobible.audio_bible_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiPathConfig implements WebMvcConfigurer {
    private static final String API_PREFIX = "/api/v1";
    private static final String ADMIN_PREFIX = API_PREFIX + "/admin";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(ADMIN_PREFIX,
                controller -> controller.isAnnotationPresent(AdminApiController.class));
        configurer.addPathPrefix(API_PREFIX,
                controller -> !controller.isAnnotationPresent(AdminApiController.class));
    }
}
