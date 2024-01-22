package ifmo.csr;

import ifmo.dto.UserEntityDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @Operation(summary = "Добавить событие в интересующие",
            security = {@SecurityRequirement(name = "bearer-key")})
    @PostMapping(value = "/{event_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<HttpStatus> addEventToInteresting(@PathVariable(value = "event_id") @Min(1) long eventId,
                                                             @RequestHeader("Username") String userLogin,
                                                             HttpServletRequest request) {
        meetingService.addEventToInteresting(eventId, userLogin, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Получить пользоватаелей, заинтересованных событием",
            security = {@SecurityRequirement(name = "bearer-key")})
    @GetMapping(value = "/{event_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<UserEntityDto>> getInterestedUsers(@PathVariable(value = "event_id") @Min(1) long eventId,
                                                                  HttpServletRequest request) {
        var interestedUsers = meetingService.getAllUsersByEvent(eventId, request);
        return ResponseEntity.ok().body(interestedUsers);
    }

}
