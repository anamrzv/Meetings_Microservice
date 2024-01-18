package ifmo.controller;


import ifmo.dto.UserEntityDto;
import ifmo.exceptions.CustomInternalException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.RemoteInvocationResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final AmqpTemplate amqpTemplate;
    private static final String exchanger = "direct-exchange";
    private static final String showUserKey = "show-user";
    private static final String updateUserIdKey = "show-user-id";

    @Operation(summary = "Получить данные пользователя по логину")
    @GetMapping("/{login}")
    public ResponseEntity<UserEntityDto> getUser(@PathVariable String login, @RequestHeader(value = "Authorization") String authorizationHeader) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, showUserKey, login);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((UserEntityDto) answer);
    }

    @Operation(summary = "Получить данные пользователя по id")
    @GetMapping("/by/id/{id}")
    public ResponseEntity<UserEntityDto> getUserById(@PathVariable long id, @RequestHeader(value = "Authorization") String authorizationHeader) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, updateUserIdKey, id);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((UserEntityDto) answer);

    }
}
