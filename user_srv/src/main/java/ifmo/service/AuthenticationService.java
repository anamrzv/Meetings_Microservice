package ifmo.service;

import ifmo.repository.RoleRepository;
import ifmo.repository.UserRepository;
import ifmo.exceptions.CustomExistsException;
import ifmo.exceptions.CustomNotFoundException;
import ifmo.model.UserEntity;
import ifmo.dto.AuthenticationRequest;
import ifmo.dto.AuthenticationResponse;
import ifmo.dto.RegisterRequest;
import ifmo.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@EnableRabbit
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private static final String registerQueue = "register-queue";
    private static final String registerAdminQueue = "register-admin-queue";
    private static final String authQueue = "auth-queue";

    @RabbitListener(queues = registerQueue)
    public AuthenticationResponse register(RegisterRequest req) {
        var user = userService.createUserWithProfile(req);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @RabbitListener(queues = registerAdminQueue)
    public AuthenticationResponse registerAdmin(RegisterRequest req) {
        try {
            var user = UserEntity.builder()
                    .login(req.getLogin())
                    .hashedPassword(passwordEncoder.encode(req.getPassword()))
                    .role(roleRepository.findByName("ROLE_ADMIN"))
                    .build();
            userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (RuntimeException e) {
            throw new CustomExistsException("Пожалуйста, выберите другой логин");
        }
    }

    @RabbitListener(queues = authQueue)
    public AuthenticationResponse authenticate(AuthenticationRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getLogin(),
                            req.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new CustomNotFoundException("Пользователь с этими данными не найден, проверьте логин и пароль");
        }
        var user = userRepository.findByLogin(req.getLogin()).orElseThrow(() -> new CustomNotFoundException("Пользователь с этими данными не найден, проверьте логин/пароль"));
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
