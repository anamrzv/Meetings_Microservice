package ifmo.controller;


import ifmo.dto.UserEntityDto;
import ifmo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получить данные пользователя по логину")
    @GetMapping("/{login}")
    public ResponseEntity<UserEntityDto> getUser(@PathVariable String login, @RequestHeader(value = "Authorization") String authorizationHeader) {
        return ResponseEntity.ok().body(userService.getUser(login));
    }

    @Operation(summary = "Получить данные пользователя по id")
    @GetMapping("/by/id/{id}")
    public ResponseEntity<UserEntityDto> getUserById(@PathVariable long id, @RequestHeader(value = "Authorization") String authorizationHeader) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

}
