package com.example.part35teammonew.config;

import com.example.part35teammonew.jwt.JwtAuthenticationEntryPoint;
import com.example.part35teammonew.jwt.JwtSecurityConfig;
import com.example.part35teammonew.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {
  private final TokenProvider tokenProvider;
  private final CorsFilter corsFilter;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // token을 사용하는 방식이기 때문에 csrf를 disable 함
        .csrf(AbstractHttpConfigurer::disable)

        .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
        .exceptionHandling(exceptionHandling -> exceptionHandling
            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
        )

        .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
            .requestMatchers(
                "/",
                "/index.html",
                "/assets/**",
                "/favicon.ico",
                "/api/users",
                "/api/users/{userId}",
                "/api/users/{userId}/hard",
                "/api/articles",
                "/api/articles2",
                "/api/articles/restore",
                "/api/articles/{articleId}/article-views",
                "/api/articles/{articleId}",
                "/api/articles/{articleId}/hard",
                "/api/comments/{commentId}/comment-likes",
                "/api/interests",
                "/api/interests/{interestsId}",
                "/api/interests/{interestId}/subscriptions",
                "api/comments",
                "api/comments/{commentId}",
                "/api/comments/{commentId}/hard",
                "/api/user-activities/**",
                "/api/notifications/{notificationId}",
                "/api/notifications"

            ).permitAll()
            .anyRequest().authenticated()
        )

        // 세션을 사용하지 않기 때문에 STATELESS로 설정
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )

        // enable h2-console
        .headers(headers ->
            headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
        )

        .with(new JwtSecurityConfig(tokenProvider), customizer -> {});
    return http.build();
  }
}