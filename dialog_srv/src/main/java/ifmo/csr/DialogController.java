package ifmo.csr;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.DialogEntityDto;
import ifmo.dto.UserEntityDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/dialog")
@RequiredArgsConstructor
public class DialogController {

    private final DialogService dialogService;

    @Operation(summary = "Получить все чаты пользователя",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping(value = "/",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private Flux<ResponseEntity<List<ChatEntityDto>>> getAllChatsByUser(@RequestHeader("Username") String userLogin,
                                                                        HttpServletRequest request) {
        var chats = dialogService.getAllChatsByUserLogin(userLogin, request);
        return Flux.just(ResponseEntity.ok().body(chats));
    }

    @Operation(summary = "Сохранить чат пользователя",
            security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping(value = "/",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private Mono<ResponseEntity<HttpStatus>> saveChatUser(@RequestBody DialogEntityDto dto) {
        dialogService.saveChatUser(dto.getChatId(), dto.getUserId());
        return Mono.just(ResponseEntity.ok().build());
    }

    @Operation(summary = "Получить собеседников чата",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping(value = "/users/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private Flux<ResponseEntity<List<UserEntityDto>>> getAllUsersByChat(@PathVariable Long id,
                                                                        HttpServletRequest request) {
        var users = dialogService.getAllUsersByChat(id, request);
        return Flux.just(ResponseEntity.ok().body(users));
    }
}
