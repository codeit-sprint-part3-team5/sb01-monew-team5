package com.example.part35teammonew.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Monew API 문서")
            .description("5팀의 Monew 서비스의 전체 API 문서입니다."))
        .servers(List.of(new Server()
            .url("http://localhost:8080")
            .description("로컬 서버")));
  }
}
