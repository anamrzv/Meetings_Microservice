package ifmo.controller;

import ifmo.dto.ProfileEntityDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final AmqpTemplate amqpTemplate;
    private static final String exchanger = "direct-exchange";
    private static final String showProfileKey = "show-profile";
    private static final String updateProfileKey = "update-profile";


    @GetMapping(value = "/{profile_id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ProfileEntityDto> showProfileById(@PathVariable(value = "profile_id") @Min(1) long profileId) {
        ProfileEntityDto answer = (ProfileEntityDto) amqpTemplate.convertSendAndReceive(exchanger, showProfileKey, profileId);
        return ResponseEntity.ok().body(answer);
    }

    @PutMapping(value = "/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<ProfileEntityDto> updateProfile(@Valid @RequestBody ProfileEntityDto changedProfile,
                                                           @RequestHeader("Username") String userLogin) {
        changedProfile.setSecondUser(userLogin);
        ProfileEntityDto updatedUserProfile = (ProfileEntityDto) amqpTemplate.convertSendAndReceive(exchanger, updateProfileKey, changedProfile);
        return ResponseEntity.ok().body(updatedUserProfile);
    }
}
