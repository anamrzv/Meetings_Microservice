package ifmo.integration.services;

import ifmo.utils.JsonDeserializer;
import ifmo.dto.ProfileEntityDto;
import ifmo.model.ProfileEntity;
import ifmo.repository.ProfileRepository;
import ifmo.repository.UserRepository;
import ifmo.service.ProfileService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

@SpringBootTest(classes = ProfileService.class)
public class UserProfileServiceTest {
    @Autowired
    private ProfileService profileService;
    @MockBean
    private ProfileRepository profileRepository;
    @MockBean
    private UserRepository userRepository;

    @DisplayName("Test profile")
    @Test
    public void showUserProfiles() {
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setId(1L);

        Mockito.when(profileRepository.getProfileEntityById(1L)).thenReturn(Optional.of(profileEntity));
        ProfileEntityDto profileEntityDto = profileService.showUserProfile(1L);

        String expected = JsonDeserializer.objectToJson(new ProfileEntityDto(profileEntity));
        String actual = JsonDeserializer.objectToJson(profileEntityDto);

        Assertions.assertEquals(expected, actual);
    }
}
