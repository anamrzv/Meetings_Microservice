package ifmo.feign_client;

import ifmo.dto.ChatEntityDto;
import ifmo.dto.DialogEntityDto;
import ifmo.dto.UserEntityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "dialog-srv-eureka-client", path = "/api/v1/dialog")
public interface DialogClient {

    @GetMapping("/")
    ResponseEntity<List<ChatEntityDto>> getAllChatsByUser(@RequestHeader("Username") String userLogin);

    default ResponseEntity<List<ChatEntityDto>> getAllChatsByUserFallback() {
        return ResponseEntity.internalServerError().body(null);
    }

    @GetMapping("/users/{id}")
    ResponseEntity<List<UserEntityDto>> getAllUsersByChat(@PathVariable Long id);

    @PostMapping("/")
    ResponseEntity<HttpStatus> saveChatUser(@RequestBody DialogEntityDto dto);
}
