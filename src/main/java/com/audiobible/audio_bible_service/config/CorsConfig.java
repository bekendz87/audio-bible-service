package com.audiobible.audio_bible_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.stream(allowedOrigins.split(","))
            .map(String::trim).filter(origin -> !origin.isBlank()).toList());
        config.setAllowedHeaders(List.of("Origin", "Content-Type", "Accept", "Authorization", "Range"));
        config.setExposedHeaders(List.of("Content-Range", "Content-Length", "Accept-Ranges"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}