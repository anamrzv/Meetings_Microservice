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
                .route("user-route", r -> r.path("/api/v1/user/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user_srv"))
                .route("profile-route", r -> r.path("/api/v1/profile/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user_srv"))
                .route("auth-route", r -> r.path("/api/v1/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user_srv"))
                .build();
    }
}
