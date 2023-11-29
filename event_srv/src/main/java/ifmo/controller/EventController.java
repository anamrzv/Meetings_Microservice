package ifmo.controller;

import ifmo.dto.EventEntityDto;
import ifmo.exceptions.custom.UnsuccessfulSave;
import ifmo.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private static final int START_OF_JWT_TOKEN = 7;

    @GetMapping(value = "/",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<Page<EventEntityDto>> getAllEvents(@RequestParam(value = "offset", defaultValue = "0") @Min(0) Integer offset,
                                                              @RequestParam(value = "limit", defaultValue = "50") @Min(1) @Max(100) Integer limit) {
        var events = eventService.findAll(PageRequest.of(offset, limit));
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("X-Total-Count", String.valueOf(events.getTotalElements()));

        return ResponseEntity.ok().headers(responseHeaders).body(events);
    }

    @GetMapping(value = "/child",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<List<EventEntityDto>> getAllChildEvents() {
        var events = eventService.findAllChild();
        return ResponseEntity.ok().body(events);
    }

    @PostMapping(value = "/filter",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<List<EventEntityDto>> getEventsByCharacteristics(@RequestBody Set<String> chars) {
        var events = eventService.getEventsByCharacteristics(chars);
        return ResponseEntity.ok().body(events);
    }

    @GetMapping(value = "/{event_id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<EventEntityDto> getEventById(@PathVariable(value = "event_id") @Min(1) long eventId) {
        var addedEvent = eventService.findEventById(eventId);
        return ResponseEntity.ok().body(addedEvent);
    }

//    @PostMapping(value = "/{event_id}",
//            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
//            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    private ResponseEntity<HttpStatus> addEventToInteresting(@PathVariable(value = "event_id") @Min(1) long eventId,
//                                                             @RequestHeader("Authorization") String request) {
//        var jwt = request.substring(START_OF_JWT_TOKEN);
//        var userLogin = jwtService.extractUserLogin(jwt);
//        userService.addEventToInteresting(eventId, userLogin);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @PostMapping(value = "/",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<EventEntityDto> addNewEvent(@Valid @RequestBody EventEntityDto newEvent) {
        try {
            var addedEvent = eventService.addEvent(newEvent);
            return ResponseEntity.ok().body(addedEvent);
        } catch (IllegalArgumentException e) {
            throw new UnsuccessfulSave("Не удалось добавить событие");
        }
    }

    @PutMapping(value = "/{event_id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<EventEntityDto> updateEvent(@Valid @RequestBody EventEntityDto changedEvent,
                                                       @PathVariable(value = "event_id") @Min(1) long eventId) {
        var updatedEvent = eventService.updateEvent(eventId, changedEvent);
        return ResponseEntity.ok().body(updatedEvent);
    }

    @PostMapping(value = "/characteristics/{event_id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<HttpStatus> addEventCharacteristics(@RequestBody Set<String> chars,
                                                               @PathVariable(value = "event_id") @Min(1) long eventId) {
        eventService.setCharacteristicsToEvent(eventId, chars);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/characteristics/{event_id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    private ResponseEntity<HttpStatus> removeEventCharacteristics(@RequestBody Set<String> chars,
                                                                  @PathVariable(value = "event_id") @Min(1) long eventId) {
        eventService.removeCharacteristicsFromEvent(eventId, chars);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}

