package ifmo;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.UserEntityDto;
import ifmo.feign_client.ChatClient;
import ifmo.feign_client.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatUserController {

    private final ChatUserService chatUserService;

    @Autowired
    private final UserClient userClient;

    @Autowired
    private final ChatClient chatClient;

    @GetMapping(value = "/",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<List<ChatEntityDto>> getAllChatsByUser(@RequestHeader("Username") String userLogin) {
        var chats = chatUserService.getAllChatsByUserLogin(userLogin);
        return ResponseEntity.ok().body(chats);
    }

    @GetMapping(value = "/users/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<List<UserEntityDto>> getAllUsersByChat(@RequestParam Long id) {
        var users = chatUserService.getAllUsersByChat(id);
        return ResponseEntity.ok().body(users);
    }

//    @PostMapping(value = "/{second_user}",
//            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
//            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    public ResponseEntity<ChatEntityDto> createChatWithUser(@PathVariable(value = "second_user") String secondUserLogin,
//                                                            @RequestHeader("Username") String userLogin,
//                                                            @RequestBody String message) {
//        var chat = chatUserService.createChat(userLogin, secondUserLogin, message);
//        return ResponseEntity.ok().body(chat);
//    }
}
