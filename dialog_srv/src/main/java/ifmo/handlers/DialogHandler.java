package ifmo.handlers;

import ifmo.csr.DialogService;
import ifmo.dto.ChatEntityDto;
import ifmo.dto.UserEntityDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.List;
//TODO: проверка со switchIfEmpty
@Component
public class DialogHandler {
    private DialogService dialogService;

    public DialogHandler(DialogService dialogService) {
        this.dialogService = dialogService;
    }

    public Mono<ResponseEntity<List<ChatEntityDto>>> getAllChatsByUser(ServerRequest serverRequest) {
        var userLogin = serverRequest.pathVariable("login");
        var chats = dialogService.getAllChatsByUserLogin(userLogin);
        return Mono.just(ResponseEntity.ok().body(chats));
    }

    public Mono<ResponseEntity<HttpStatus>> saveChatUser(ServerRequest serverRequest) {
        var chatId = Long.parseLong(serverRequest.pathVariable("chat_id"));
        var userId = Long.parseLong(serverRequest.pathVariable("user_id"));
        dialogService.saveChatUser(chatId, userId);
        return Mono.just(ResponseEntity.ok().build());
    }

    private Mono<ResponseEntity<List<UserEntityDto>>> getAllUsersByChat(ServerRequest serverRequest) {
        var id = Long.parseLong(serverRequest.pathVariable("id"));
        var users = dialogService.getAllUsersByChat(id);
        return Mono.just(ResponseEntity.ok().body(users));
    }
}
