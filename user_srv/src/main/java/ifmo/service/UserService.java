package ifmo.service;

import ifmo.dto.UserEntityDto;
import ifmo.exceptions.CustomNotFoundException;
import ifmo.repository.ProfileRepository;
import ifmo.repository.RoleRepository;
import ifmo.repository.UserRepository;
import ifmo.exceptions.CustomExistsException;
import ifmo.model.ProfileEntity;
import ifmo.model.UserEntity;
import ifmo.dto.RegisterRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@EnableRabbit
@RequiredArgsConstructor
public class UserService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private static final String showUserQueue = "show-user-queue";
    private static final String showUserIdQueue = "show-user-id-queue";

    @RabbitListener(queues = showUserQueue)
    public UserEntityDto getUser(String login) {
        System.out.println("got "+login);
        var user = userRepository.findByLogin(login).orElseThrow(() -> new CustomNotFoundException("Юзер не найден"));
        return new UserEntityDto(user);
    }

    @RabbitListener(queues = showUserIdQueue)
    public UserEntityDto getUserById(long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new CustomNotFoundException("Юзер не найден"));
        return new UserEntityDto(user);
    }

    @Transactional
    public UserEntity createUserWithProfile(RegisterRequest req) {
        try {
            var profile = ProfileEntity.builder()
                    .mail(req.getMail())
                    .firstName(req.getFirstName())
                    .lastName(req.getLastName())
                    .dateOfBirth(req.getDateOfBirth())
                    .phone(req.getPhone())
                    .mail(req.getMail())
                    .icon(req.getIcon())
                    .build();
            var savedProfile = profileRepository.save(profile);
            var user = UserEntity.builder()
                    .login(req.getLogin())
                    .hashedPassword(passwordEncoder.encode(req.getPassword()))
                    .role(Period.between(req.getDateOfBirth(), LocalDate.now()).getYears() >= 18 ? roleRepository.findByName("ROLE_ADULT") : roleRepository.findByName("ROLE_CHILD"))
                    .profileId(savedProfile)
                    .build();
            return userRepository.save(user);
        } catch (ConstraintViolationException c) {
            throw c;
        } catch (RuntimeException e) {
            throw new CustomExistsException("Пожалуйста, выберите другой логин");
        }
    }

}
