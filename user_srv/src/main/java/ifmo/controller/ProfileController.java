package ifmo.controller;

import ifmo.dto.ProfileEntityDto;
import ifmo.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "Показать данные профиля",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping(value = "/{profile_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ProfileEntityDto> showProfileById(@PathVariable(value = "profile_id") @Min(1) long profileId) {
        var gotProfile = profileService.showUserProfile(profileId);
        return ResponseEntity.ok().body(gotProfile);
    }

    @Operation(summary = "Обновить профиль",
            security = {@SecurityRequirement(name = "bearer-key")})
    @PutMapping(value = "/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<ProfileEntityDto> updateProfile(@Valid @RequestBody ProfileEntityDto changedProfile,
                                                            @RequestHeader("Username") String userLogin) {
        var updatedUserProfile = profileService.updateUserProfile(userLogin, changedProfile);
        return ResponseEntity.ok().body(updatedUserProfile);
    }
}
