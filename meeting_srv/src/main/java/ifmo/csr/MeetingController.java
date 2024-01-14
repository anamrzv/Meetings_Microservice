package ifmo.csr;

import ifmo.dto.UserEntityDto;
import ifmo.dto.UtilDto;
import ifmo.exceptions.CustomInternalException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.RemoteInvocationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/meetings")
@RequiredArgsConstructor
public class MeetingController {

    private final AmqpTemplate amqpTemplate;
    private static final String exchanger = "direct-exchange";
    private static final String addKey = "add";
    private static final String getKey = "get";

    @PostMapping(value = "/{event_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<HttpStatus> addEventToInteresting(@PathVariable(value = "event_id") @Min(1) long eventId,
                                                             @RequestHeader("Username") String userLogin,
                                                             HttpServletRequest request) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, addKey, new UtilDto(eventId, userLogin, request.getHeader("Authorization")));
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{event_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<UserEntityDto>> getInterestedUsers(@PathVariable(value = "event_id") @Min(1) long eventId,
                                                                  HttpServletRequest request) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, getKey, new UtilDto(eventId, "", request.getHeader("Authorization")));
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((List<UserEntityDto>) answer);
    }

}
