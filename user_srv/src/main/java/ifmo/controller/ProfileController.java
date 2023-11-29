package ifmo.controller;

import ifmo.dto.ProfileEntityDto;
import ifmo.security.JwtService;
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
    private final JwtService jwtService;

    private static final int START_OF_JWT_TOKEN = 7;

    @GetMapping(value = "/{profile_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ProfileEntityDto> showUserByProfile(@PathVariable(value = "profile_id") @Min(1) long profileId) {
        var gotProfile = profileService.showUserProfile(profileId);
        return ResponseEntity.ok().body(gotProfile);
    }

    @PutMapping(value = "/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<ProfileEntityDto> updatedProfile(@Valid @RequestBody ProfileEntityDto changedProfile,
                                                      @RequestHeader("Authorization") String request) {
        var jwt = request.substring(START_OF_JWT_TOKEN);
        var userLogin = jwtService.extractUserLogin(jwt);
        var updatedUserProfile = profileService.updateUserProfile(userLogin, changedProfile);
        return ResponseEntity.ok().body(updatedUserProfile);
    }
}
