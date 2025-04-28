package com.example.part35teammonew.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin("\"http://localhost:5173"); // 이 포트만 허용
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");

    source.registerCorsConfiguration("/api/**", config);  // /api/** 경로에만 CORS 적용
    return new CorsFilter(source);
  }
}
