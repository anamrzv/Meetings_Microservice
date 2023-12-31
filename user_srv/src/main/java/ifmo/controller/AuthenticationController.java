package ifmo.controller;

import ifmo.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ifmo.requests.AuthenticationRequest;
import ifmo.requests.AuthenticationResponse;
import ifmo.requests.RegisterRequest;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RefreshScope
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authenticationService.register(req));
    }

    @PostMapping("/register_admin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(authenticationService.registerAdmin(req));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest req) {
        return ResponseEntity.ok(authenticationService.authenticate(req));
    }
}
