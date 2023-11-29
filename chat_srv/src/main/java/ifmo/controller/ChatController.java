package ifmo.controller;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.MessageDTO;
import ifmo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    //private final JwtService jwtService;

    private static final int START_OF_JWT_TOKEN = 7;

    @GetMapping(value = "/{chat_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<Page<MessageDTO>> getAllChatMessages(@PathVariable(value = "chat_id") long chatId) {
        Pageable wholePage = Pageable.unpaged();
        var messages = chatService.getAllMessagesByChatId(chatId, wholePage);
        return ResponseEntity.ok().body(messages);
    }

//    @PostMapping(value = "/{chat_id}",
//            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
//            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    private ResponseEntity<MessageDTO> sendMessageToChat(@RequestHeader("Authorization") String request,
//                                                         @PathVariable(value = "chat_id") long chatId,
//                                                         @RequestBody String message) {
//        var jwt = request.substring(START_OF_JWT_TOKEN);
//        var userLogin = jwtService.extractUserLogin(jwt);
//        var msgDto = chatService.addMessageToChat(chatId, userLogin, message);
//        return ResponseEntity.ok().body(msgDto);
//    }

//    @GetMapping(value = "/",
//            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    private ResponseEntity<List<ChatEntityDto>> getAllChatsByUser(@RequestHeader("Authorization") String request) {
//        var jwt = request.substring(START_OF_JWT_TOKEN);
//        var userLogin = jwtService.extractUserLogin(jwt);
//        var chats = chatService.getAllChatsByUserLogin(userLogin);
//        return ResponseEntity.ok().body(chats);
//    }
}
