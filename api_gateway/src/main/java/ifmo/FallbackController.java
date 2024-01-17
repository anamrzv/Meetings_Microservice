package ifmo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping(path = "/chatFail", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> chatFail() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Circuit Breaker handler detected problem on chat");
    }

    @RequestMapping(path = "/eventFail", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<String> eventFail() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Circuit Breaker handler detected problem on event");
    }

    @RequestMapping(path = "/dialogFail", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<String> dialogFail() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Circuit Breaker handler detected problem on dialog");
    }

    @RequestMapping(path = "/meetingFail", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<String> meetingFail() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Circuit Breaker handler detected problem on meeting");
    }

    @RequestMapping(path = "/userFail", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<String> userFail() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Circuit Breaker handler detected problem on user");
    }
}
