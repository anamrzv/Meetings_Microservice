package ifmo.controller;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.MessageDTO;
import ifmo.feign_client.UserClient;
import ifmo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class MessageController {

    private final ChatService chatService;

    private final UserClient userClient;

    @GetMapping(value = "/{chat_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<Page<MessageDTO>> getAllChatMessages(@PathVariable(value = "chat_id") long chatId) {
        Pageable wholePage = Pageable.unpaged();
        var messages = chatService.getAllMessagesByChatId(chatId, wholePage);
        return ResponseEntity.ok().body(messages);
    }

    @GetMapping(value = "/entity/{chat_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<ChatEntityDto> getChat(@PathVariable(value = "chat_id") long chatId) {
        var chat = chatService.getChatById(chatId);
        return ResponseEntity.ok().body(chat);
    }

    @PostMapping(value = "/{chat_id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<MessageDTO> sendMessageToChat(@RequestHeader("Username") String userLogin,
                                                         @PathVariable(value = "chat_id") long chatId,
                                                         @RequestBody String message) {
        var sender = userClient.getUser(userLogin);
        var msgDto = chatService.addMessageToChat(chatId, Objects.requireNonNull(sender.getBody()).getId(), message);
        return ResponseEntity.ok().body(msgDto);
    }

    @PostMapping(value = "/{second_user}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ChatEntityDto> createChatWithUser(@PathVariable(value = "second_user") String secondUserLogin,
                                                            @RequestHeader("Username") String userLogin,
                                                            @RequestBody String message) {
        var chat = chatService.createChat(userLogin, secondUserLogin, message);
        return ResponseEntity.ok().body(chat);
    }
}
