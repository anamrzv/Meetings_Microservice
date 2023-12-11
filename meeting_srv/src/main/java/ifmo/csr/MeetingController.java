package ifmo.csr;

import ifmo.dto.UserEntityDto;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping(value = "/{event_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<HttpStatus> addEventToInteresting(@PathVariable(value = "event_id") @Min(1) long eventId,
                                                             @RequestHeader("Username") String userLogin) {
        meetingService.addEventToInteresting(eventId, userLogin);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{event_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<UserEntityDto>> getInterestedUsers(@PathVariable(value = "event_id") @Min(1) long eventId) {
        var interestedUsers = meetingService.getAllUsersByEvent(eventId);
        return ResponseEntity.ok().body(interestedUsers);
    }

}
