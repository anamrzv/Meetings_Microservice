package ifmo.controller;


import ifmo.dto.UserEntityDto;
import ifmo.exceptions.custom.TokenExpired;
import ifmo.security.JwtService;
import ifmo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/{login}")
    public ResponseEntity<UserEntityDto> getUser(@PathVariable String login, @RequestHeader(value = "Authorization") String authorizationHeader) {
        if (jwtService.isTokenValid(authorizationHeader)) {
            return ResponseEntity.ok().body(userService.getUser(login));
        } else throw new TokenExpired("Ваш токен истек, требуется перелогиниться");
    }

    @GetMapping("/by/id/{id}")
    public ResponseEntity<UserEntityDto> getUserById(@PathVariable long id, @RequestHeader(value = "Authorization") String authorizationHeader) {
        if (jwtService.isTokenValid(authorizationHeader)) {
            return ResponseEntity.ok().body(userService.getUserById(id));
        } else throw new TokenExpired("Ваш токен истек, требуется перелогиниться");
    }

}
