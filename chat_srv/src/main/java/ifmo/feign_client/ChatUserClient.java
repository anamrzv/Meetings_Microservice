package ifmo.feign_client;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.UserEntityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "chat_user-srv-eureka-client", path = "/api/v1/chats")
public interface ChatUserClient {

    @GetMapping("/")
    ResponseEntity<List<ChatEntityDto>> getAllChatsByUser(@RequestHeader("Username") String userLogin);

    @GetMapping("/users/{id}")
    ResponseEntity<List<UserEntityDto>> getAllUsersByChat(@RequestParam Long id);
}
