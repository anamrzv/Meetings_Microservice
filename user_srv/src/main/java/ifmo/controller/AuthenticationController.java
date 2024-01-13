package ifmo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ifmo.dto.AuthenticationRequest;
import ifmo.dto.AuthenticationResponse;
import ifmo.dto.RegisterRequest;


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

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest req) {
        AuthenticationResponse answer = (AuthenticationResponse) amqpTemplate.convertSendAndReceive(exchanger, registerKey, req);
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/register_admin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@Valid @RequestBody RegisterRequest req) {
        AuthenticationResponse answer = (AuthenticationResponse) amqpTemplate.convertSendAndReceive(exchanger, registerAdminKey, req);
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest req) {
        AuthenticationResponse answer = (AuthenticationResponse) amqpTemplate.convertSendAndReceive(exchanger, authKey, req);
        return ResponseEntity.ok(answer);
    }
}
