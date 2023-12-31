package ifmo.service;

import ifmo.exceptions.CustomNotFoundException;
import ifmo.repository.ProfileRepository;
import ifmo.repository.UserRepository;
import ifmo.dto.ProfileEntityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileEntityDto showUserProfile(Long profileId) {
        var profileEntity = profileRepository.getProfileEntityById(profileId).orElseThrow(() -> new CustomNotFoundException("Профиль пользователя не найден"));
        return new ProfileEntityDto(profileEntity);
    }

    public ProfileEntityDto updateUserProfile(String login, ProfileEntityDto profileDTO) {
        var user = userRepository.findByLogin(login).orElseThrow(() -> new CustomNotFoundException("Пользователь не найден"));

        var oldProfile = profileRepository.findById(user.getProfileId().getId()).orElseThrow(() -> new CustomNotFoundException("Профиль пользователя не найден"));
        oldProfile.setFirstName(profileDTO.getFirstName());
        oldProfile.setLastName(profileDTO.getLastName());
        oldProfile.setMail(profileDTO.getMail());
        oldProfile.setIcon(profileDTO.getIcon());
        oldProfile.setPhone(profileDTO.getPhone());

        profileRepository.save(oldProfile);
        return new ProfileEntityDto(oldProfile);
    }
}
