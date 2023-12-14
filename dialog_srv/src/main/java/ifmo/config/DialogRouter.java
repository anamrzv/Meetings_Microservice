package ifmo.config;

import ifmo.handlers.DialogHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class DialogRouter {
    @Bean
    public RouterFunction<ServerResponse> route(DialogHandler dialogHandler) {
        RequestPredicate routeToAllChatByUser = RequestPredicates.
                GET("/api/v1/dialog/")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
        RequestPredicate routeToSaveChatUser = RequestPredicates.
                POST("/api/v1/dialog/")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));
        RequestPredicate routeToAllUsersByChat = RequestPredicates
                .GET("/api/v1/dialog/users/{id:[0-9]+}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML));

        return RouterFunctions
                .route(routeToAllChatByUser, dialogHandler::getAllChatsByUser)
                .andRoute(routeToSaveChatUser, dialogHandler::saveChatUser)
                .andRoute(routeToAllUsersByChat, dialogHandler::getAllUsersByChat);
    }
}
