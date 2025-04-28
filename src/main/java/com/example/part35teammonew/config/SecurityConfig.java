package com.example.part35teammonew.config;

//<<<<<<< HEAD
import com.example.part35teammonew.jwt.JwtAuthenticationEntryPoint;
import com.example.part35teammonew.jwt.JwtSecurityConfig;
import com.example.part35teammonew.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
//=======
//>>>>>>> 35d8652d231b0a352e6f9a60d2dc3e761df62df5
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // csrf를 disable 함 -> 회원가입 시 403 에러 해결
                .csrf(AbstractHttpConfigurer::disable)

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
                            "/api/user-activities/{userId}",
                            "/api/notifications/{notificationId}",
                            "/api/notifications"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        return http.build();
    }
}