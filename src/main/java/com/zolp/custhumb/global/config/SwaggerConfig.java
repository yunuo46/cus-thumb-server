package com.zolp.custhumb.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@OpenAPIDefinition(
        servers = {
                @Server(url = "https://planpal-server-remote-772190012442.asia-northeast3.run.app", description = "리모트 서버"),
                @Server(url = "https://planpal-server-local-772190012442.asia-northeast3.run.app", description = "로컬 서버")
        })
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("planpal REST API")
                        .version("1.0")
                        .description("planpal REST API 명세서입니다."));
    }

    @Bean
    public GroupedOpenApi api() {
        String[] paths = {"/api/**"};
        String[] packagesToScan = {"com.gdg.planpal.domain"};
        return GroupedOpenApi.builder()
                .group("planpal-openapi")
                .pathsToMatch(paths)
                .packagesToScan(packagesToScan)
                .build();
    }
}
