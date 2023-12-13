package ifmo.config;

import ifmo.handlers.DialogHandler;
import ifmo.handlers.RouterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RequestPredicates.*;

@Configuration
public class DialogRouter {
    @Bean
    public RouterFunction<ServerResponse> route(DialogHandler dialogHandler) {
        return RouterFunctions
                .route(
                        GET("/api/v1/dialog/")
                                .and(accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)), dialogHandler::getAllChatsByUser)
                .andRoute(
                    POST("/api/v1/dialog/").
                            and(accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML), dialogHandler::saveChatUser)
                )
                .andRoute(
                        GET("/api/v1/dialog/users/{id:[0-9]+}")
                                .and(accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML), dialogHandler::getAllUsersByChat)
                );
    }
}
