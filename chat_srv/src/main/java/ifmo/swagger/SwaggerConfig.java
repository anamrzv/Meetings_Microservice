package ifmo.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicUserApi() {
        return GroupedOpenApi.builder()
                .group("meetingApp")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI().info(new Info().title("Meetings API")
                        .version("version 1.0.0")
                        .contact(new Contact().name("dasxunya")))
                .servers(List.of(
                        new Server().url("http://localhost:9005")
                                .description("Сервис чата"),
                        new Server().url("http://localhost:9004")
                                .description("Сервис диалога"),
                        new Server().url("http://localhost:9003")
                                .description("Сервис событий"),
                        new Server().url("http://localhost:9002")
                                .description("Сервис встреч"),
                        new Server().url("http://localhost:9001")
                                .description("Сервис пользователя")));
    }
}
