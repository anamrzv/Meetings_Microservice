package ifmo.csr;

import ifmo.csr.ChatUserService;
import ifmo.dto.ChatEntityDto;
import ifmo.dto.ChatUserDto;
import ifmo.dto.UserEntityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatUserController {

    private final ChatUserService chatUserService;


    @GetMapping(value = "/",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<List<ChatEntityDto>> getAllChatsByUser(@RequestHeader("Username") String userLogin) {
        var chats = chatUserService.getAllChatsByUserLogin(userLogin);
        return ResponseEntity.ok().body(chats);
    }

    @PostMapping(value = "/",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<HttpStatus> saveChatUser(@RequestBody ChatUserDto dto) {
        chatUserService.saveChatUser(dto.getChatId(), dto.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/users/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<List<UserEntityDto>> getAllUsersByChat(@PathVariable Long id) {
        var users = chatUserService.getAllUsersByChat(id);
        return ResponseEntity.ok().body(users);
    }
}

