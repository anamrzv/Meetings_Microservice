package ifmo.csr;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.DialogEntityDto;
import ifmo.dto.UserEntityDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import ifmo.dto.UtilDto;
import ifmo.exceptions.CustomInternalException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.RemoteInvocationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Objects;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/dialog")
@RequiredArgsConstructor
public class DialogController {

    private final AmqpTemplate amqpTemplate;
    private static final String exchanger = "direct-exchange";
    private static final String chatKey = "chat";
    private static final String saveKey = "save";
    private static final String userKey = "user";

    @Operation(summary = "Получить все чаты пользователя",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping(value = "/",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private Mono<ResponseEntity<List<ChatEntityDto>>> getAllChatsByUser(@RequestHeader("Username") String userLogin,
                                                                        @RequestHeader(value = "Authorization") String authorizationHeader) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, chatKey, new UtilDto(userLogin, authorizationHeader, 0L, 0L));
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return Mono.just(ResponseEntity.ok().body((List<ChatEntityDto>) answer));
    }

    @Operation(summary = "Сохранить чат пользователя",
            security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping(value = "/",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private Mono<ResponseEntity<HttpStatus>> saveChatUser(@RequestBody DialogEntityDto dto) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, saveKey, new UtilDto("", "", dto.getChatId(), dto.getUserId()));
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return Mono.just(ResponseEntity.ok().build());
    }

    @Operation(summary = "Получить собеседников чата",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping(value = "/users/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private Mono<ResponseEntity<List<UserEntityDto>>> getAllUsersByChat(@PathVariable Long id,
                                                                        @RequestHeader(value = "Authorization") String authorizationHeader) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, userKey, new UtilDto("", authorizationHeader, id, 0L));
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return Mono.just(ResponseEntity.ok().body((List<UserEntityDto>) answer));
    }
}
