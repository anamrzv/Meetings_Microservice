package ifmo.handlers;

import ifmo.csr.DialogService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

//TODO: проверка со switchIfEmpty
@Component
public class DialogHandler {
    private final DialogService dialogService;

    public DialogHandler(DialogService dialogService) {
        this.dialogService = dialogService;
    }

    public Mono<ServerResponse> getAllChatsByUser(ServerRequest serverRequest) {
        String login = serverRequest.pathVariable("login");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dialogService.getAllChatsByUserLogin(login), List.class);
    }

    public Mono<ServerResponse> saveChatUser(ServerRequest serverRequest) {
        var chatId = Long.parseLong(serverRequest.pathVariable("chat_id"));
        var userId = Long.parseLong(serverRequest.pathVariable("user_id"));
        dialogService.saveChatUser(chatId, userId);
        return ServerResponse.ok().build();
    }

    public Mono<ServerResponse> getAllUsersByChat(ServerRequest serverRequest) {
        var id = Long.parseLong(serverRequest.pathVariable("id"));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dialogService.getAllUsersByChat(id), List.class);
    }
}
