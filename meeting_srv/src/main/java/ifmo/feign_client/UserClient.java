package ifmo.feign_client;

import ifmo.dto.UserEntityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-srv-eureka-client", path = "/api/v1/user")
public interface UserClient {

    @GetMapping("/{login}")
    ResponseEntity<UserEntityDto> getUser(@PathVariable String login, @RequestHeader(value = "Authorization") String authorizationHeader);

    @GetMapping("/by/id/{id}")
    ResponseEntity<UserEntityDto> getUserById(@PathVariable Long id, @RequestHeader(value = "Authorization") String authorizationHeader);

    default ResponseEntity<UserEntityDto> getUserFallback() {
        return ResponseEntity.internalServerError().body(null);
    }
}
