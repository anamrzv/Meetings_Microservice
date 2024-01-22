package ifmo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableWebSecurity
@OpenAPIDefinition(
        info = @Info(
                title = "Сервис диалога",
                version = "1.0.0",
                description = "Содержит операции для проведения диалога между пользователями",
                contact = @Contact(
                        name = "anamrzv",
                        url = "https://vk.com/ana.munn"
                )
        )
)
public class DialogSrvApplication {

    public static void main(String[] args) {
        SpringApplication.run(DialogSrvApplication.class, args);
    }

    @Bean
    @ConditionalOnMissingBean
    public CorsConfiguration corsConfiguration() {
        var configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedMethods(List.of("GET", "POST"));
        return configuration;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
