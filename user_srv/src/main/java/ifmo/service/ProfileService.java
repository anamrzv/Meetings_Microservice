package ifmo.service;

import ifmo.dto.IconRequest;
import ifmo.exceptions.CustomNotFoundException;
import ifmo.repository.ProfileRepository;
import ifmo.repository.UserRepository;
import ifmo.dto.ProfileEntityDto;
import ifmo.validator.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    private static final String showProfileQueue = "show-profile-queue";
    private static final String updateProfileQueue = "update-profile-queue";
    private static final String uploadIconQueue = "upload-icon-queue";
    private static final String getIconQueue = "get-icon-queue";

    @RabbitListener(queues = showProfileQueue, returnExceptions = "true")
    public ProfileEntityDto showUserProfile(Long profileId) {
        var profileEntity = profileRepository.getProfileEntityById(profileId).orElseThrow(() -> new CustomNotFoundException("Профиль пользователя не найден"));
        return new ProfileEntityDto(profileEntity);
    }

    @RabbitListener(queues = updateProfileQueue, returnExceptions = "true")
    public ProfileEntityDto updateUserProfile(ProfileEntityDto profileDTO) {
        var user = userRepository.findByLogin(profileDTO.getSecondUser()).orElseThrow(() -> new CustomNotFoundException("Пользователь не найден"));

        var oldProfile = profileRepository.findById(user.getProfileId().getId()).orElseThrow(() -> new CustomNotFoundException("Профиль пользователя не найден"));
        oldProfile.setFirstName(profileDTO.getFirstName());
        oldProfile.setLastName(profileDTO.getLastName());
        oldProfile.setMail(profileDTO.getMail());
        oldProfile.setPhone(profileDTO.getPhone());

        profileRepository.save(oldProfile);
        return new ProfileEntityDto(oldProfile);
    }

    @RabbitListener(queues = uploadIconQueue, returnExceptions = "true")
    public boolean uploadIcon(IconRequest iconRequest) {
        var user = userRepository.findByLogin(iconRequest.getLogin()).orElseThrow(() -> new CustomNotFoundException("Пользователь не найден"));
        user.getProfileId().setIcon(ImageUtil.compressImage(iconRequest.getFile()));
        profileRepository.save(user.getProfileId());
        return true;
    }

    @RabbitListener(queues = getIconQueue, returnExceptions = "true")
    @Transactional
    public byte[] getIcon(String login) {
        var user = userRepository.findByLogin(login).orElseThrow(() -> new CustomNotFoundException("Пользователь не найден"));
        return ImageUtil.decompressImage(user.getProfileId().getIcon());
    }
}
