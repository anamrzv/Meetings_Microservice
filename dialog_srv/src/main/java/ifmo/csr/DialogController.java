package ifmo.csr;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.DialogEntityDto;
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
public class DialogController {

    private final DialogService dialogService;


    @GetMapping(value = "/",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<List<ChatEntityDto>> getAllChatsByUser(@RequestHeader("Username") String userLogin) {
        var chats = dialogService.getAllChatsByUserLogin(userLogin);
        return ResponseEntity.ok().body(chats);
    }

    @PostMapping(value = "/",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<HttpStatus> saveChatUser(@RequestBody DialogEntityDto dto) {
        dialogService.saveChatUser(dto.getChatId(), dto.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/users/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<List<UserEntityDto>> getAllUsersByChat(@PathVariable Long id) {
        var users = dialogService.getAllUsersByChat(id);
        return ResponseEntity.ok().body(users);
    }
}

