package ifmo.controller;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.MessageDTO;
import ifmo.exceptions.CustomInternalException;
import ifmo.feign_client.UserClient;
import ifmo.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class MessageController {

    private final ChatService chatService;

    private final UserClient userClient;

    private final CircuitBreakerFactory circuitBreakerFactory;

    @GetMapping(value = "/{chat_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<List<MessageDTO>> getAllChatMessages(@PathVariable(value = "chat_id") long chatId) {
        List<MessageDTO> messages = chatService.getAllMessagesByChatId(chatId);
        return ResponseEntity.ok().body(messages);
    }

    @GetMapping(value = "/entity/{chat_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<ChatEntityDto> getChat(@PathVariable(value = "chat_id") long chatId) {
        var chat = chatService.getChatById(chatId);
        return ResponseEntity.ok().body(chat);
    }

    @PostMapping(value = "/msg/{chat_id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<MessageDTO> sendMessageToChat(@RequestHeader("Username") String userLogin,
                                                         @PathVariable(value = "chat_id") long chatId,
                                                         @RequestBody String message,
                                                         HttpServletRequest request) {
        CircuitBreaker breaker = circuitBreakerFactory.create("eren");
        var sender = breaker.run(() -> userClient.getUser(userLogin, request.getHeader("Authorization")), throwable -> userClient.getUserFallback());
        if (sender.getStatusCode().is5xxServerError())
            throw new CustomInternalException("Пожалуйста, повторите попытку позже :)");
        var msgDto = chatService.addMessageToChat(chatId, Objects.requireNonNull(sender.getBody()).getId(), message);
        return ResponseEntity.ok().body(msgDto);
    }

    @PostMapping(value = "/{second_user}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ChatEntityDto> createChatWithUser(@PathVariable(value = "second_user") String secondUserLogin,
                                                            @RequestHeader("Username") String userLogin,
                                                            @RequestBody String message,
                                                            HttpServletRequest request) {
        var chat = chatService.createChat(userLogin, secondUserLogin, message, request);
        return ResponseEntity.ok().body(chat);
    }
}
