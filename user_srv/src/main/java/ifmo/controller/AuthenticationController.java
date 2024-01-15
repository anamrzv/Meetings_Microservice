package ifmo.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import ifmo.exceptions.CustomInternalException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.RemoteInvocationResult;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ifmo.dto.AuthenticationRequest;
import ifmo.dto.AuthenticationResponse;
import ifmo.dto.RegisterRequest;

import java.util.Objects;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RefreshScope
public class AuthenticationController {

    private final AmqpTemplate amqpTemplate;
    private static final String exchanger = "direct-exchange";
    private static final String registerKey = "register";
    private static final String registerAdminKey = "register-admin";
    private static final String authKey = "auth";

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest req) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, registerKey, req);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((AuthenticationResponse) answer);
    }

    @Operation(summary = "Регистрация администратора")
    @PostMapping("/register_admin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@Valid @RequestBody RegisterRequest req) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, registerAdminKey, req);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((AuthenticationResponse) answer);
    }

    @Operation(summary = "Аутентификация")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest req) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, authKey, req);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((AuthenticationResponse) answer);
    }
}
