package com.search.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Search Service API")
                        .version("1.0")
                        .description("Train Search Service"))
                .servers(List.of(
                        new Server().url("http://localhost:8080/search").description("Gateway"),
                        new Server().url("http://localhost:8087").description("Direct")
                ));
    }
}
