package ifmo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableHystrix
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter filter;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                //swagger
                .route("chat-openapi", r -> r.path("/chat/v3/api-docs")
                        .filters(f -> f.rewritePath("/chat/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://chat-srv-eureka-client"))
                .route("user-openapi", r -> r.path("/user/v3/api-docs")
                        .filters(f -> f.rewritePath("/user/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://user-srv-eureka-client"))
                .route("dialog-openapi", r -> r.path("/dialog/v3/api-docs")
                        .filters(f -> f.rewritePath("/dialog/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://dialog-srv-eureka-client"))
                .route("meeting-openapi", r -> r.path("/meeting/v3/api-docs")
                        .filters(f -> f.rewritePath("/meeting/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://meeting-srv-eureka-client"))
                .route("event-openapi", r -> r.path("/event/v3/api-docs")
                        .filters(f -> f.rewritePath("/event/v3/api-docs", "/v3/api-docs"))
                        .uri("lb://event-srv-eureka-client"))

                .route("user-route", r -> r.path("/api/v1/user/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-srv-eureka-client"))
                .route("profile-route", r -> r.path("/api/v1/profile/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-srv-eureka-client"))
                .route("auth-route", r -> r.path("/api/v1/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-srv-eureka-client"))
                .route("chat-route", r -> r.path("/api/v1/chats/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://chat-srv-eureka-client"))
                .route("dialog-route", r -> r.path("/api/v1/dialog/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://dialog-srv-eureka-client"))
                .route("event-route", r -> r.path("/api/v1/events/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://event-srv-eureka-client"))
                .route("meeting-route", r -> r.path("/api/v1/meetings/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://meeting-srv-eureka-client"))
                .build();
    }
}
