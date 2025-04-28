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
                // 임시 TODO 추후 이것들 지우기
                "/api/articles",
                "/api/articles/restore",
                "/api/articles/{articleId}/article-views",
                "/api/articles/{articleId}",
                "/api/articles/{articleId}/hard",
                "/{interestId}",
                "/{interestId}/subscriptions"
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