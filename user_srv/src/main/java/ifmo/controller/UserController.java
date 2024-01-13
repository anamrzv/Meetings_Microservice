package ifmo.controller;


import ifmo.dto.UserEntityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final AmqpTemplate amqpTemplate;
    private static final String exchanger = "direct-exchange";
    private static final String showUserKey = "show-user";
    private static final String updateUserIdKey = "show-user-id";

    @GetMapping("/{login}")
    public ResponseEntity<UserEntityDto> getUser(@PathVariable String login, @RequestHeader(value = "Authorization") String authorizationHeader) {
        UserEntityDto answer = (UserEntityDto) amqpTemplate.convertSendAndReceive(exchanger, showUserKey, login);
        return ResponseEntity.ok().body(answer);
    }

    @GetMapping("/by/id/{id}")
    public ResponseEntity<UserEntityDto> getUserById(@PathVariable long id, @RequestHeader(value = "Authorization") String authorizationHeader) {
        UserEntityDto answer = (UserEntityDto) amqpTemplate.convertSendAndReceive(exchanger, updateUserIdKey, id);
        return ResponseEntity.ok().body(answer);
    }

}
