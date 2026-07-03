package com.saramageste.taskboard.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taskBoardOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Board API")
                        .description("API for Kanban-style task management")
                        .version("1.0.0"));
    }

}
