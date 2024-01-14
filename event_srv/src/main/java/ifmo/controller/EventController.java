package ifmo.controller;

import ifmo.dto.CharacteristicsDto;
import ifmo.dto.EventEntityDto;
import ifmo.exceptions.CustomBadRequestException;
import ifmo.exceptions.CustomInternalException;
import ifmo.exceptions.CustomNotFoundException;
import ifmo.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.RemoteInvocationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final JwtService jwtService;

    private final AmqpTemplate amqpTemplate;

    private static final String exchanger = "direct-exchange";
    private static final String findAllKey = "find-all";
    private static final String findChildKey = "find-child";
    private static final String filterKey = "filter";
    private static final String idKey = "id";
    private static final String addKey = "add";
    private static final String updateKey = "update";
    private static final String setKey = "set";
    private static final String removeKey = "remove";

    @GetMapping(value = "/",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<Page<EventEntityDto>> getAllEvents(@RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
                                                              @RequestParam(value = "limit", defaultValue = "50") @Min(1) @Max(100) Integer limit,
                                                              @RequestHeader(value = "Authorization") String authorizationHeader
    ) {
        if (jwtService.extractRole(authorizationHeader).contains("ROLE_ADULT")) {
            var answer = amqpTemplate.convertSendAndReceive(exchanger, findAllKey, PageRequest.of(offset, limit));
            if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
            if (answer.getClass().isInstance(new RemoteInvocationResult())) {
                var a = (RemoteInvocationResult) answer;
                throw (RuntimeException) Objects.requireNonNull(a.getException());
            }
            var a = (Page<EventEntityDto>) answer;
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("X-Total-Count", String.valueOf(a.getTotalElements()));

            return ResponseEntity.ok().headers(responseHeaders).body(a);
        } else throw new CustomNotFoundException("Для вас ничего не найдено :(");
    }

    @GetMapping(value = "/child",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<List<EventEntityDto>> getAllChildEvents(@RequestHeader(value = "Authorization") String authorizationHeader) {
       var answer = amqpTemplate.convertSendAndReceive(exchanger, findChildKey, "");
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((List<EventEntityDto>) answer);

    }

    @PostMapping(value = "/filter",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<List<EventEntityDto>> getEventsByCharacteristics(@RequestBody Set<String> chars,
                                                                            @RequestHeader(value = "Authorization") String authorizationHeader) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, filterKey, new CharacteristicsDto(chars, null));
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((List<EventEntityDto>) answer);
    }

    @GetMapping(value = "/{event_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<EventEntityDto> getEventById(@PathVariable(value = "event_id") @Min(1) long eventId,
                                                        @RequestHeader(value = "Authorization") String authorizationHeader) {
        var answer = amqpTemplate.convertSendAndReceive(exchanger, idKey, eventId);
        if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
        if (answer.getClass().isInstance(new RemoteInvocationResult())) {
            var a = (RemoteInvocationResult) answer;
            throw (RuntimeException) Objects.requireNonNull(a.getException());
        }
        return ResponseEntity.ok().body((EventEntityDto) answer);
    }

    @PostMapping(value = "/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<EventEntityDto> addNewEvent(@Valid @RequestBody EventEntityDto newEvent,
                                                       @RequestHeader(value = "Authorization") String authorizationHeader) {
        if (jwtService.extractRole(authorizationHeader).contains("ROLE_ADMIN")) {
            try {
                var answer = amqpTemplate.convertSendAndReceive(exchanger, addKey, newEvent);
                if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
                if (answer.getClass().isInstance(new RemoteInvocationResult())) {
                    var a = (RemoteInvocationResult) answer;
                    throw (RuntimeException) Objects.requireNonNull(a.getException());
                }
                return ResponseEntity.ok().body((EventEntityDto) answer);
            } catch (IllegalArgumentException e) {
                throw new CustomBadRequestException("Не удалось добавить событие");
            }
        } else throw new CustomBadRequestException("У вас недостаточно прав на совершение этого действия");

    }

    @PutMapping(value = "/{event_id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<EventEntityDto> updateEvent(@Valid @RequestBody EventEntityDto changedEvent,
                                                       @PathVariable(value = "event_id") @Min(1) long eventId,
                                                       @RequestHeader(value = "Authorization") String authorizationHeader) {
        if (jwtService.extractRole(authorizationHeader).contains("ROLE_ADMIN")) {
            changedEvent.setId(eventId);
            var answer = amqpTemplate.convertSendAndReceive(exchanger, updateKey, changedEvent);
            if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
            if (answer.getClass().isInstance(new RemoteInvocationResult())) {
                var a = (RemoteInvocationResult) answer;
                throw (RuntimeException) Objects.requireNonNull(a.getException());
            }
            return ResponseEntity.ok().body((EventEntityDto) answer);
        } else throw new CustomBadRequestException("У вас недостаточно прав на совершение этого действия");
    }

    @PostMapping(value = "/characteristics/{event_id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<HttpStatus> addEventCharacteristics(@RequestBody Set<String> chars,
                                                               @PathVariable(value = "event_id") @Min(1) long eventId,
                                                               @RequestHeader(value = "Authorization") String authorizationHeader) {
        if (jwtService.extractRole(authorizationHeader).contains("ROLE_ADMIN")) {
            var answer = amqpTemplate.convertSendAndReceive(exchanger, setKey, new CharacteristicsDto(chars, eventId));
            if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
            if (answer.getClass().isInstance(new RemoteInvocationResult())) {
                var a = (RemoteInvocationResult) answer;
                throw (RuntimeException) Objects.requireNonNull(a.getException());
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else throw new CustomBadRequestException("У вас недостаточно прав на совершение этого действия");
    }

    @DeleteMapping(value = "/characteristics/{event_id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<HttpStatus> removeEventCharacteristics(@RequestBody Set<String> chars,
                                                                  @PathVariable(value = "event_id") @Min(1) long eventId,
                                                                  @RequestHeader(value = "Authorization") String authorizationHeader) {
        if (jwtService.extractRole(authorizationHeader).contains("ROLE_ADMIN")) {
            var answer = amqpTemplate.convertSendAndReceive(exchanger, removeKey, new CharacteristicsDto(chars, eventId));
            if (answer == null) throw new CustomInternalException("Сервер не отвечает. Пожалуйста, попробуйте позже");
            if (answer.getClass().isInstance(new RemoteInvocationResult())) {
                var a = (RemoteInvocationResult) answer;
                throw (RuntimeException) Objects.requireNonNull(a.getException());
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else throw new CustomBadRequestException("У вас недостаточно прав на совершение этого действия");
    }


}
