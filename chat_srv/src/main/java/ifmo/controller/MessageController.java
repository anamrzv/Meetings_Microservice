package ifmo.controller;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.CreateChatDto;
import ifmo.dto.MessageDTO;
import ifmo.exceptions.CustomInternalException;
import ifmo.feign_client.UserClient;
import ifmo.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.RemoteInvocationResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Objects;

@Tag(name = "Контроллер сообщений", description = "Действия, связанные с чатом пользователей")
@CrossOrigin
@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class MessageController {

    private final AmqpTemplate amqpTemplate;

    private static final String exchanger = "direct-exchange";
    private static final String findAllKey = "find";
    private static final String sendKey = "send";
    private static final String createKey = "create";
    private static final String idKey = "id";

    @Operation(summary = "Получить все сообщения из чата",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping(value = "/{chat_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<List<MessageDTO>> getAllChatMessages(@PathVariable(value = "chat_id") long chatId) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, findAllKey, chatId);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((List<MessageDTO>) answer);
    }

    @Operation(summary = "Получить чат пользователя",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping(value = "/entity/{chat_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<ChatEntityDto> getChat(@PathVariable(value = "chat_id") long chatId,
                                                  @RequestHeader(value = "Authorization") String authorizationHeader) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, idKey, chatId);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((ChatEntityDto) answer);
    }

    @Operation(summary = "Отправить сообщение в чат",
            security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping(value = "/msg/{chat_id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<MessageDTO> sendMessageToChat(@RequestHeader("Username") String userLogin,
                                                         @PathVariable(value = "chat_id") long chatId,
                                                         @RequestBody String message,
                                                         HttpServletRequest request) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, sendKey, new CreateChatDto(userLogin, "", message, request.getHeader("Authorization"), chatId));
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((MessageDTO) answer);
    }

    @Operation(summary = "Создать чат с пользователем",
            security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping(value = "/{second_user}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ChatEntityDto> createChatWithUser(@PathVariable(value = "second_user") String secondUserLogin,
                                                            @RequestHeader("Username") String userLogin,
                                                            @RequestBody String message,
                                                            HttpServletRequest request) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, createKey, new CreateChatDto(userLogin, secondUserLogin, message, request.getHeader("Authorization"), null));
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((ChatEntityDto) answer);
    }
}
