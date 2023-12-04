package ifmo.feign_client;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.ChatUserDto;
import ifmo.dto.UserEntityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "chat_user-srv-eureka-client", path = "/api/v1/chats")
public interface ChatUserClient {

    @GetMapping("/")
    ResponseEntity<List<ChatEntityDto>> getAllChatsByUser(@RequestHeader("Username") String userLogin);

    @GetMapping("/users/{id}")
    ResponseEntity<List<UserEntityDto>> getAllUsersByChat(@PathVariable Long id);

    @PostMapping("/")
    ResponseEntity<HttpStatus> saveChatUser(@RequestBody ChatUserDto dto);
}
