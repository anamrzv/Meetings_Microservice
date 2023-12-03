package ifmo.feign_client;

import ifmo.dto.UserEntityDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-srv-eureka-client", path = "/api/v1/user")
public interface UserClient {

    @GetMapping("/{login}")
    public ResponseEntity<UserEntityDto> getUser(@PathVariable String login);
}
