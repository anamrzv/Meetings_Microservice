package ifmo.controller;


import ifmo.dto.UserEntityDto;
import ifmo.security.JwtService;
import ifmo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
//    private final ChatService chatService;

    private static final int START_OF_JWT_TOKEN = 7;

    @GetMapping("/{login}")
    public ResponseEntity<UserEntityDto> getUser(@PathVariable String login) {
        return ResponseEntity.ok().body(userService.getUser(login));
    }

//    @GetMapping(value = "/{event_id}",
//            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    public ResponseEntity<List<UserEntityDto>> getInterestedUsers(@PathVariable(value = "event_id") @Min(1) long eventId) {
//        var interestedUsers = userService.getAllUsersByEvent(eventId);
//        return ResponseEntity.ok().body(interestedUsers);
//    }

//    @PostMapping(value = "/{second_user}",
//            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
//            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
//    public ResponseEntity<ChatEntityDto> createChatWithUser(@PathVariable(value = "second_user") String secondUserLogin,
//                                                            @RequestHeader("Authorization") String request,
//                                                            @RequestBody String message) {
//        var jwt = request.substring(START_OF_JWT_TOKEN);
//        var userLogin = jwtService.extractUserLogin(jwt);
//        var chat = chatService.createChat(userLogin, secondUserLogin, message);
//        return ResponseEntity.ok().body(chat);
//    }
//

}
