package ifmo.service;

import ifmo.dto.*;
import ifmo.exceptions.CustomBadRequestException;
import ifmo.model.ProfileEntity;
import ifmo.repository.RoleRepository;
import ifmo.repository.UserRepository;
import ifmo.exceptions.CustomExistsException;
import ifmo.exceptions.CustomNotFoundException;
import ifmo.model.UserEntity;
import ifmo.security.JwtService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
    private final JavaMailSender mailSender;
    @Value("${mailing.site}")
    private String siteURL;

    private static final String registerQueue = "register-queue";
    private static final String registerAdminQueue = "register-admin-queue";
    private static final String authQueue = "auth-queue";
    private static final String verifyQueue = "verify-queue";

    @RabbitListener(queues = registerQueue, returnExceptions = "true")
    public boolean register(RegisterRequest req) throws jakarta.mail.MessagingException, IOException {
        var user = userService.createUserWithProfile(req);
        sendVerificationEmail(user);
        return true;
    }

    @RabbitListener(queues = verifyQueue, returnExceptions = "true")
    public boolean verifyUser(String code) {
        var user = userRepository.findByVerificationCode(code);
        if (user.isEmpty()) return false;
        if (user.get().isEnabled()) return false;
        user.get().setEnabled(true);
        user.get().setVerificationCode(null);
        userRepository.save(user.get());
        return true;
    }

    @RabbitListener(queues = registerAdminQueue, returnExceptions = "true")
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
        if (user.isEnabled()) {
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }
        else throw new CustomBadRequestException("Пожалуйста, подтвердите почту, чтобы начать пользоваться нашим сервисом");
    }

    private void sendVerificationEmail(UserEntity user)
            throws MessagingException, jakarta.mail.MessagingException {
        ProfileEntity profile = user.getProfileId();
        String toAddress = profile.getMail();
        String fromAddress = "ana.mrzv@mail.ru";
        String subject = "Подтверждение регистрации";
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

        MimeMessage message = mailSender.createMimeMessage();

        message.setFrom(new InternetAddress(fromAddress));
        message.setRecipients(MimeMessage.RecipientType.TO, toAddress);
        message.setSubject(subject);

        String htmlContent ="Здравствуйте, " + profile.getFirstName() +"!"
                + "<p>Пожалуйста, нажмите на ссылку ниже для подтверждения регистрации:</p>"
                + "<h3><a href=\"" + verifyURL + "\" target=\"_self\">VERIFY</a></h3>"
                + "Спасибо за регистрацию,<br>"
                + "Ваш Meeting App :)";
        message.setContent(htmlContent, "text/html; charset=utf-8");

        mailSender.send(message);
    }
}
