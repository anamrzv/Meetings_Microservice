package ifmo.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import ifmo.exceptions.CustomInternalException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.RemoteInvocationResult;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ifmo.dto.AuthenticationRequest;
import ifmo.dto.AuthenticationResponse;
import ifmo.dto.RegisterRequest;

import java.util.Objects;


@Controller
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RefreshScope
public class AuthenticationController {

    private final AmqpTemplate amqpTemplate;
    private static final String exchanger = "direct-exchange";
    private static final String registerKey = "register";
    private static final String registerAdminKey = "register-admin";
    private static final String authKey = "auth";
    private static final String verifyKey = "verify";

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, registerKey, req);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body("Проверьте почту для подтверждения регистрации");
    }

    @Operation(summary = "Верификация пользователя")
    @GetMapping("/verify")
    public String verify(@Param("code") String code, Model model) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, verifyKey, code);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        var success = (Boolean) answer;
        String pageTitle;
        if (success) {
            pageTitle = "Аккаунт подтвержден!";
            model.addAttribute("pageTitle", pageTitle);
            return "verify_success";
        }
        else {
            pageTitle = "Аккаунт не подтвержден";
            model.addAttribute("pageTitle", pageTitle);
            return "verify_fail";
        }
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
