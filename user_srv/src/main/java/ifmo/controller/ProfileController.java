package ifmo.controller;

import ifmo.dto.ProfileEntityDto;
import ifmo.service.ProfileService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping(value = "/{profile_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ProfileEntityDto> showUserByProfile(@PathVariable(value = "profile_id") @Min(1) long profileId) {
        var gotProfile = profileService.showUserProfile(profileId);
        return ResponseEntity.ok().body(gotProfile);
    }

    @PutMapping(value = "/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<ProfileEntityDto> updatedProfile(@Valid @RequestBody ProfileEntityDto changedProfile,
                                                            @RequestHeader("Username") String userLogin) {
        var updatedUserProfile = profileService.updateUserProfile(userLogin, changedProfile);
        return ResponseEntity.ok().body(updatedUserProfile);
    }
}
