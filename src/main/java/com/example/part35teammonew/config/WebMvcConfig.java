package com.example.part35teammonew.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{spring:\\w+}")
                .setViewName("forward:/index.html");

        registry.addViewController("/**/{spring:\\w+}")
                .setViewName("forward:/index.html");

        registry.addViewController("/{spring:\\w+}/**{spring:?!(\\.js|\\.css|\\.png|\\.jpg|\\.jpeg|\\.svg|\\.woff2)?}")
                .setViewName("forward:/index.html");
    }
}
