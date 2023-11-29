package ifmo.service;

import ifmo.repository.ProfileRepository;
import ifmo.repository.RoleRepository;
import ifmo.repository.UserRepository;
import ifmo.exceptions.CustomExistsException;
import ifmo.model.ProfileEntity;
import ifmo.model.UserEntity;
import ifmo.requests.RegisterRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

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
